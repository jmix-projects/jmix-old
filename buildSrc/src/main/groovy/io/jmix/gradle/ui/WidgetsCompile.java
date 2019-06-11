/*
 * Copyright 2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jmix.gradle.ui;

import com.google.common.base.Joiner;
import org.apache.commons.io.FileUtils;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.*;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.FileUtils.iterateFiles;

public class WidgetsCompile extends WidgetsTask {

    protected static final String GWT_XML_EXTENSION = "gwt.xml";
    protected static final String VAADIN_WIDGETSETS_MANIFEST_KEY = "Vaadin-Widgetsets";

    @Input
    protected String widgetSetsDir = "";
    @Input
    protected String widgetSetClass = "";
    @Input
    protected Map<String, Object> compilerArgs = new HashMap<>();
    @Input
    protected boolean strict = true;
    @Input
    protected boolean draft = false;
    @Input
    protected boolean disableCastChecking = false;
    @Input
    protected int optimize = 9;
    @Input
    protected String style = "OBF";

    protected boolean generateWidgetSetFile = false;

    protected String xmx = "-Xmx768m";
    protected String xss = "-Xss8m";

    protected String logLevel = "ERROR";

    protected int workers = Math.max(Runtime.getRuntime().availableProcessors() - 1, 1);

    protected boolean printCompilerClassPath = false;

    public WidgetsCompile() {
        setDescription("Builds GWT widgetset");
        setGroup("web");
    }

    public void generate(String widgetSetClass) {
        this.generateWidgetSetFile = true;
        this.widgetSetClass = widgetSetClass;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @TaskAction
    public void compileWidgets() {
        if (widgetSetsDir == null || widgetSetsDir.isEmpty()) {
            widgetSetsDir = getDefaultBuildDir();
        }

        if (widgetSetClass == null || widgetSetClass.isEmpty()) {
            // try to find
            widgetSetClass = getDefaultWidgetSet();
            if (widgetSetClass == null) {
                throw new GradleException("Unable to find .gwt.xml file for widgets compilation");
            }
        }

        File widgetSetsDirectory = new File(widgetSetsDir);
        if (widgetSetsDirectory.exists()) {
            deleteQuietly(widgetSetsDirectory);
        }

        // strip gwt-unitCache
        File gwtTemp = getProject().file("build/gwt");
        if (!gwtTemp.exists()) {
            gwtTemp.mkdir();
        }

        File gwtJavaTmp = getProject().file("build/tmp/" + getName());
        if (gwtJavaTmp.exists()) {
            deleteQuietly(gwtJavaTmp);
        }
        gwtJavaTmp.mkdirs();

        File gwtWidgetSetTemp = new File(gwtTemp, "widgetset");
        gwtWidgetSetTemp.mkdir();

        List<File> compilerClassPath = collectClassPathEntries();
        List<String> gwtCompilerArgs = collectCompilerArgs(gwtWidgetSetTemp.getAbsolutePath());
        List<String> gwtCompilerJvmArgs = collectCompilerJvmArgs(gwtJavaTmp);

        if (generateWidgetSetFile) {
            generateWidgetSetXml(compilerClassPath, widgetSetClass);
        }

        getProject().javaexec(spec -> {
            spec.setMain("com.google.gwt.dev.Compiler");
            spec.setClasspath(getProject().files(compilerClassPath));
            spec.setArgs(gwtCompilerArgs);
            spec.setJvmArgs(gwtCompilerJvmArgs);
        });

        deleteQuietly(new File(gwtWidgetSetTemp, "WEB-INF"));

        gwtWidgetSetTemp.renameTo(widgetSetsDirectory);
    }

    protected void generateWidgetSetXml(List<File> compilerClassPath, String widgetSetClass) {
        StringBuilder gwtXmlBuilder = new StringBuilder();
        gwtXmlBuilder.append("<module>");
        gwtXmlBuilder.append("<inherits name=\"io.jmix.ui.widgets.WidgetSet\"/>");
        gwtXmlBuilder.append("</module>");

        for (File file : compilerClassPath) {
            if (file.getName().endsWith(".jar")) {
                try (FileInputStream is = new FileInputStream(file);
                     JarInputStream jarStream = new JarInputStream(is)) {

                    String vaadinWidgetsets = getVaadinWidgetsets(jarStream);
                    if (vaadinWidgetsets != null) {
                        getLogger().info("[WidgetsCompile] Including widgets from {}", vaadinWidgetsets);

                        gwtXmlBuilder.append("<inherits name=\"").append(vaadinWidgetsets).append("\"/>");
                    }
                } catch (IOException e) {
                    throw new GradleException("Unable to read widgets includes", e);
                }
            }
        }

        File file = new File("" + widgetSetClass + ".gwt.xml"); // todo temp location
        try {
            FileUtils.write(file, gwtXmlBuilder.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GradleException("Unable to write generated .gwt.xml file: " + file.getAbsolutePath(), e);
        }
    }

    @Nullable
    protected String getVaadinWidgetsets(JarInputStream jarStream) {
        Manifest mf = jarStream.getManifest();
        if (mf != null && mf.getMainAttributes() != null) {
            return mf.getMainAttributes().getValue(VAADIN_WIDGETSETS_MANIFEST_KEY);
        }
        return null;
    }

    protected String getDefaultWidgetSet() {
        SourceSetContainer sourceSets = getProject().getConvention()
                .getPlugin(JavaPluginConvention.class)
                .getSourceSets();

        for (SourceSet sourceSet : sourceSets) {
            for (File srcDir : sourceSet.getResources().getSrcDirs()) {
                if (!srcDir.exists()) {
                    continue;
                }

                Iterator<File> gwtXmlFiles = iterateFiles(srcDir, new String[]{GWT_XML_EXTENSION}, true);

                if (gwtXmlFiles.hasNext()) {
                    File gwtXmlFile = gwtXmlFiles.next();

                    Path relativePath = srcDir.toPath().relativize(gwtXmlFile.toPath());
                    List<String> names = new ArrayList<>();

                    for (int i = 0; i < relativePath.getNameCount(); i++) {
                        if (i != relativePath.getNameCount() -1 ) {
                            names.add(relativePath.getName(i).toString());
                        } else {
                            names.add(relativePath.getName(i).toString().replace("." + GWT_XML_EXTENSION, ""));
                        }
                    }

                    String widgetSet = Joiner.on(".").join(names);

                    getLogger().info("[WidgetsCompile] Found WidgetSet: {} in {}", widgetSet, srcDir.getAbsolutePath());

                    return widgetSet;
                }
            }
        }

        return null;
    }

    @InputFiles
    @SkipWhenEmpty
    public FileCollection getSourceFiles() {
        return super.getSourceFiles();
    }

    @OutputDirectory
    public File getOutputDirectory() {
        if (widgetSetsDir == null || widgetSetsDir.isEmpty()) {
            return new File(getDefaultBuildDir());
        }
        return new File(widgetSetsDir);
    }

    protected String getDefaultBuildDir() {
        return new File(getProject().getBuildDir(), "web/VAADIN/widgetsets").getAbsolutePath();
    }

    protected List<File> collectClassPathEntries() {
        Set<File> compilerClassPath = new LinkedHashSet<>();

        Configuration compileConfiguration = getProject().getConfigurations().findByName("compileClasspath");
        if (compileConfiguration != null) {
            for (Project dependencyProject : collectProjectsWithDependency("vaadin-shared")) {
                SourceSet dependencyMainSourceSet = getSourceSet(dependencyProject, "main");

                compilerClassPath.addAll(dependencyMainSourceSet.getJava().getSrcDirs());
                compilerClassPath.addAll(getClassesDirs(dependencyMainSourceSet));
                compilerClassPath.add(dependencyMainSourceSet.getOutput().getResourcesDir());

                getProject().getLogger().debug(">> Widget set building Module: {}", dependencyProject.getName());
            }
        }

        SourceSet mainSourceSet = getSourceSet(getProject(), "main");

        compilerClassPath.addAll(mainSourceSet.getJava().getSrcDirs());
        compilerClassPath.addAll(mainSourceSet.getResources().getSrcDirs());
        compilerClassPath.addAll(getClassesDirs(mainSourceSet));
        compilerClassPath.add(mainSourceSet.getOutput().getResourcesDir());

        List<File> compileClassPathArtifacts = StreamSupport
                .stream(mainSourceSet.getCompileClasspath().spliterator(), false)
                .filter(f -> includedArtifact(f.getName()) && !compilerClassPath.contains(f))
                .collect(Collectors.toList());
        compilerClassPath.addAll(compileClassPathArtifacts);

        if (getProject().getLogger().isEnabled(LogLevel.DEBUG)) {
            StringBuilder sb = new StringBuilder();
            for (File classPathEntry : compilerClassPath) {
                sb.append('\t')
                        .append(classPathEntry.getAbsolutePath())
                        .append('\n');
            }

            getProject().getLogger().debug("GWT Compiler ClassPath: \n{}", sb.toString());
            getProject().getLogger().debug("");
        } else if (printCompilerClassPath) {
            StringBuilder sb = new StringBuilder();
            for (File classPathEntry : compilerClassPath) {
                sb.append('\t')
                        .append(classPathEntry.getAbsolutePath())
                        .append('\n');
            }
            System.out.println("GWT Compiler ClassPath: \n" + sb.toString());
            System.out.println();
        }

        return new ArrayList<>(compilerClassPath);
    }

    protected List<String> collectCompilerArgs(String warPath) {
        List<String> args = new ArrayList<>();

        args.add("-war");
        args.add(warPath);

        if (strict) {
            args.add("-strict");
        }

        if (draft) {
            args.add("-draftCompile");
        }

        if (disableCastChecking) {
            args.add("-XdisableCastChecking");
        }

        Map<String, String> gwtCompilerArgs = new HashMap<>();

        gwtCompilerArgs.put("-style", style);
        gwtCompilerArgs.put("-logLevel", logLevel);
        gwtCompilerArgs.put("-localWorkers", String.valueOf(workers));
        gwtCompilerArgs.put("-optimize", String.valueOf(optimize));

        if (compilerArgs != null) {
            for (Map.Entry<String, String> entry : gwtCompilerArgs.entrySet()) {
                gwtCompilerArgs.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        for (Map.Entry<String, String> entry : gwtCompilerArgs.entrySet()) {
            args.add(entry.getKey());
            args.add(entry.getValue());
        }

        args.add(widgetSetClass);

        if (getProject().getLogger().isInfoEnabled()) {
            System.out.println("GWT Compiler args: ");
            System.out.print('\t');
            System.out.println(args);
        }

        return args;
    }

    protected List<String> collectCompilerJvmArgs(File gwtJavaTmp) {
        List<String> args = new ArrayList<>(compilerJvmArgs);

        args.add(xmx);
        args.add(xss);
        args.add("-Djava.io.tmpdir=" + gwtJavaTmp.getAbsolutePath());

        if (getProject().getLogger().isInfoEnabled()) {
            System.out.println("JVM Args:");
            System.out.print('\t');
            System.out.println(args);
        }

        return args;
    }

    public void setWidgetSetsDir(String widgetSetsDir) {
        this.widgetSetsDir = widgetSetsDir;
    }

    public String getWidgetSetsDir() {
        return widgetSetsDir;
    }

    public void setWidgetSetClass(String widgetSetClass) {
        this.widgetSetClass = widgetSetClass;
    }

    public String getWidgetSetClass() {
        return widgetSetClass;
    }

    public void setCompilerArgs(Map<String, Object> compilerArgs) {
        this.compilerArgs = compilerArgs;
    }

    public Map<String, Object> getCompilerArgs() {
        return compilerArgs;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDisableCastChecking(boolean disableCastChecking) {
        this.disableCastChecking = disableCastChecking;
    }

    public boolean isDisableCastChecking() {
        return disableCastChecking;
    }

    public void setOptimize(int optimize) {
        this.optimize = optimize;
    }

    public int getOptimize() {
        return optimize;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

    public void setXmx(String xmx) {
        this.xmx = xmx;
    }

    public String getXmx() {
        return xmx;
    }

    public void setXss(String xss) {
        this.xss = xss;
    }

    public String getXss() {
        return xss;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setWorkers(int workers) {
        this.workers = workers;
    }

    public int getWorkers() {
        return workers;
    }

    public void setPrintCompilerClassPath(boolean printCompilerClassPath) {
        this.printCompilerClassPath = printCompilerClassPath;
    }

    public boolean isPrintCompilerClassPath() {
        return printCompilerClassPath;
    }
}