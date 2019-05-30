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

import com.vaadin.sass.internal.ScssContext;
import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.handler.SCSSDocumentHandlerImpl;
import com.vaadin.sass.internal.handler.SCSSErrorHandler;
import com.yahoo.platform.yui.compressor.CssCompressor;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.impldep.com.google.common.base.Splitter;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import static org.gradle.internal.impldep.org.apache.commons.io.FileUtils.deleteDirectory;
import static org.gradle.internal.impldep.org.apache.commons.io.FileUtils.forceMkdir;

public class ThemeCompile extends DefaultTask {
    public static final String VAADIN_STYLESHEETS_MANIFEST_KEY = "Vaadin-Stylesheets";
    @Input
    protected List<File> includes = new ArrayList<>();
    @Input
    protected List<String> includedAppComponentIds = new ArrayList<>();
    @Input
    protected List<String> themes = new ArrayList<>();

    @Input
    protected String scssDir = "themes";
    protected String destDir;

    @Input
    protected boolean compress = true;
    @Input
    protected boolean cleanup = true;
    @Input
    protected boolean gzip = true;

    protected List<String> excludedThemes = new ArrayList<>();
    protected List<String> excludePaths = new ArrayList<>();
    protected List<String> doNotUnpackPaths = Arrays.asList(
            "VAADIN/themes/valo/*.css",
            "VAADIN/themes/valo/*.css.gz",
            "VAADIN/themes/valo/favicon.ico",
            "VAADIN/themes/valo/util/readme.txt",
            "META-INF/**"
    );

    public ThemeCompile() {
        setDescription("Compile SCSS styles in theme");
        setGroup("Web resources");
    }

    @OutputDirectory
    public File getOutputDirectory() {
        if (destDir == null) {
            return new File(getProject().getBuildDir(), "web");
        }
        return new File(getProject().getProjectDir(), destDir);
    }

    @InputDirectory
    public File getSourceDirectory() {
        return getProject().file(scssDir);
    }

    public List<File> getIncludes() {
        return includes;
    }

    public void setIncludes(List<File> includes) {
        this.includes = includes;
    }

    public List<String> getIncludedAppComponentIds() {
        return includedAppComponentIds;
    }

    public void setIncludedAppComponentIds(List<String> includedAppComponentIds) {
        this.includedAppComponentIds = includedAppComponentIds;
    }

    public List<String> getThemes() {
        return themes;
    }

    public void setThemes(List<String> themes) {
        this.themes = themes;
    }

    public String getScssDir() {
        return scssDir;
    }

    public void setScssDir(String scssDir) {
        this.scssDir = scssDir;
    }

    public String getDestDir() {
        return destDir;
    }

    public void setDestDir(String destDir) {
        this.destDir = destDir;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public boolean isCleanup() {
        return cleanup;
    }

    public void setCleanup(boolean cleanup) {
        this.cleanup = cleanup;
    }

    public boolean isGzip() {
        return gzip;
    }

    public void setGzip(boolean gzip) {
        this.gzip = gzip;
    }

    @TaskAction
    public void buildThemes() throws IOException {
        File stylesDirectory = getSourceDirectory();
        if (!stylesDirectory.exists()) {
            throw new FileNotFoundException(String.format("Unable to find SCSS themes root directory: %s",
                    stylesDirectory.getAbsolutePath()));
        }

        File themesTmp = new File(getProject().getBuildDir(), "themes-tmp");
        if (themesTmp.exists()) {
            deleteDirectory(themesTmp);
        }
        forceMkdir(themesTmp);

        File vaadinThemesRoot = new File(themesTmp, "VAADIN/themes");
        forceMkdir(vaadinThemesRoot);

        List<String> themes = new ArrayList<>(this.themes);

        if (themes.isEmpty()) {
            getLogger().info("[ThemeCompile] scan directory '{}' for themes",
                    stylesDirectory.getAbsolutePath());

            File[] themeFolders = stylesDirectory.listFiles(pathname ->
                    pathname.isDirectory() && !pathname.getName().startsWith(".")
            );
            if (themeFolders != null) {
                Arrays.stream(themeFolders)
                        .map(File::getName)
                        .forEach(themes::add);
            }
        }

        unpackVaadinAddonsThemes(themesTmp);
        unpackThemesConfDependencies(themesTmp, vaadinThemesRoot);

        // copy includes to build dir
        for (File includeThemeDir : includes) {
            getLogger().info("[ThemeCompile] copy includes from {}", includeThemeDir.getName());

            if (!includeThemeDir.exists()) {
                throw new GradleException("Could not found include dir ${includeThemeDir.absolutePath}");
            }

            getProject().copy(copySpec ->
                    copySpec.from(includeThemeDir)
                            .into(new File(vaadinThemesRoot, includeThemeDir.getName()))
            );
        }

        for (String themeDirName : themes) {
            buildTheme(themeDirName, stylesDirectory, vaadinThemesRoot);
        }

        File destinationDirectory = getOutputDirectory();

        copyResources(themesTmp, destinationDirectory);

        if (cleanup) {
            // remove empty directories
            removeEmptyDirs(destinationDirectory);
        }

        for (String themeName : excludedThemes) {
            File themeDestDir = new File(destinationDirectory, themeName);
            getLogger().info("[ThemeCompile] excluded theme '{}'", themeName);

            FileUtils.deleteQuietly(themeDestDir);
        }

        for (String path : excludePaths) {
            File pathFile = new File(destinationDirectory, path);
            getLogger().info("[ThemeCompile] excluded path '{}'", path);

            FileUtils.deleteQuietly(pathFile);
        }
    }

    protected void unpackVaadinAddonsThemes(File themesTmp) {
        Configuration compileConfiguration = getProject().getConfigurations().getByName("compile");
        Set<ResolvedArtifact> resolvedArtifacts = compileConfiguration.getResolvedConfiguration().getResolvedArtifacts();

        resolvedArtifacts.stream()
                .map(ResolvedArtifact::getFile)
                .filter(f -> f.exists() && f.isFile() && f.getName().endsWith(".jar"))
                .forEach(jarFile -> {
                    try (InputStream is = new FileInputStream(jarFile);
                            JarInputStream jarStream = new JarInputStream(is)) {

                        String vaadinStylesheets = getVaadinStylesheets(jarStream);
                        if (vaadinStylesheets != null) {
                            getLogger()
                                    .info("[ThemeCompile] unpack Vaadin addon styles {}", jarFile.getName());

                            getProject().copy(copySpec ->
                                    copySpec.from(getProject().zipTree(jarFile))
                                            .into(themesTmp)
                                            .include("VAADIN/**"));
                        }
                    } catch (IOException e) {
                        throw new GradleException("Unable to read JAR with theme", e);
                    }
                });
    }

    protected void unpackThemesConfDependencies(File themesTmp, File vaadinThemesRoot) {
        Configuration themesConf = getProject().getConfigurations().findByName("themes");
        if (themesConf != null) {
            List<File> themeArchives = collectThemeArchives(themesConf);

            for (File archive : themeArchives) {
                getLogger().info("[ThemeCompile] unpack themes artifact {}", archive.getName());

                if (!archive.getName().contains("vaadin-themes")) {
                    getProject().copy(copySpec ->
                                    copySpec.from(getProject().zipTree(archive))
                                        .into(vaadinThemesRoot)
                                        .setExcludes(doNotUnpackPaths)
                    );
                } else {
                    getProject().copy(copySpec ->
                                    copySpec.from(getProject().zipTree(archive))
                                            .into(themesTmp)
                                            .include("VAADIN/**")
                                            .setExcludes(doNotUnpackPaths)
                    );
                }
            }
        }
    }

    protected List<File> collectThemeArchives(Configuration themesConf) {
        Set<ResolvedDependency> firstLevelModuleDependencies =
                themesConf.getResolvedConfiguration().getFirstLevelModuleDependencies();

        List<File> files = new ArrayList<>();
        Set<ResolvedArtifact> passedArtifacts = new HashSet<>();

        for (ResolvedDependency dependency : firstLevelModuleDependencies) {
            collectThemeArchives(dependency, passedArtifacts, files);
        }

        return files;
    }

    protected void collectThemeArchives(ResolvedDependency dependency, Set<ResolvedArtifact> passedArtifacts,
                                        List<File> files) {
        for (ResolvedDependency child : dependency.getChildren()) {
            collectThemeArchives(child, passedArtifacts, files);
        }

        for (ResolvedArtifact artifact : dependency.getModuleArtifacts()) {
            if (passedArtifacts.contains(artifact)) {
                continue;
            }

            passedArtifacts.add(artifact);
            files.add(artifact.getFile());
        }
    }

    void buildTheme(String themeDirName, File stylesDirectory, File vaadinThemesRoot) throws FileNotFoundException {
        getLogger().info("[ThemeCompile] build theme '{}'", themeDirName);

        File themeDir = new File(stylesDirectory, themeDirName);
        if (!themeDir.exists()) {
            throw new FileNotFoundException("Unable to find theme directory: " + themeDir.getAbsolutePath());
        }

        File themeBuildDir = new File(vaadinThemesRoot, themeDirName);

        getLogger().info("[ThemeCompile] copy theme '{}' to build directory", themeDir.getName());
        // copy theme to build directory
        getProject().copy(copySpec ->
                copySpec.from(themeDir)
                        .into(themeBuildDir)
                        .exclude(element -> {
                            return element.getFile().getName().startsWith(".");
                        }));

        prepareAppComponentsInclude(themeBuildDir);

        getLogger().info("[ThemeCompile] compile theme '{}'", themeDir.getName());

        File scssFile = new File(themeBuildDir, "styles.scss");
        File cssFile = new File(themeBuildDir, "styles.css");

        compileScss(scssFile, cssFile);

        if (compress) {
            performCssCompression(themeDir, cssFile);
        }

        if (gzip) {
            createGzipCss(themeBuildDir, cssFile);
        }

        getLogger().info("[ThemeCompile] successfully compiled theme '{}'", themeDir.getName());
    }

    protected void compileScss(File scssFile, File cssFile) {
        ScssContext.UrlMode urlMode = ScssContext.UrlMode.MIXED;
        SCSSErrorHandler errorHandler = new SCSSErrorHandler() {
            boolean[] hasErrors = new boolean[]{false};

            @Override
            public void error(CSSParseException e) throws CSSException {
                getLogger().error("[ThemeCompile] Error when parsing file \n{} on line {}, column {}",
                        e.getURI(), e.getLineNumber(), e.getColumnNumber(), e);

                hasErrors[0] = true;
            }

            @Override
            public void fatalError(CSSParseException e) throws CSSException {
                getLogger().error("[ThemeCompile] Error when parsing file \n{} on line {}, column {}",
                        e.getURI(), e.getLineNumber(), e.getColumnNumber(), e);

                hasErrors[0] = true;
            }

            @Override
            public void warning(CSSParseException e) throws CSSException {
                getLogger().error("[ThemeCompile] Warning when parsing file \n{} on line {}, column {}",
                        e.getURI(), e.getLineNumber(), e.getColumnNumber(), e);
            }

            @Override
            public void traverseError(Exception e) {
                getLogger().error("[ThemeCompile] Error on SCSS traverse", e);

                hasErrors[0] = true;
            }

            @Override
            public void traverseError(String message) {
                getLogger().error("[ThemeCompile] {}", message);

                hasErrors[0] = true;
            }

            @Override
            public boolean isErrorsDetected() {
                return super.isErrorsDetected() || hasErrors[0];
            }
        };
        errorHandler.setWarningsAreErrors(false);

        try {
            ScssStylesheet scss =
                    ScssStylesheet.get(scssFile.getAbsolutePath(), null, new SCSSDocumentHandlerImpl(), errorHandler);

            if (scss == null) {
                throw new GradleException("Unable to find SCSS file " + scssFile.getAbsolutePath());
            }

            scss.compile(urlMode);

            Writer writer = new FileWriter(cssFile);
            scss.write(writer, false);
            writer.close();
        } catch (Exception e) {
            throw new GradleException("Unable to build theme " + scssFile.getAbsolutePath(), e);
        }

        if (errorHandler.isErrorsDetected()) {
            throw new GradleException("Unable to build theme " + scssFile.getAbsolutePath());
        }
    }

    protected void createGzipCss(File themeBuildDir, File cssFile) {
        getLogger().info("[ThemeCompile] compress css file 'styles.css'");

        File cssGzFile = new File(themeBuildDir, "styles.css.gz");

        try (FileInputStream uncompressedStream = new FileInputStream(cssFile);
             GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(cssGzFile))) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = uncompressedStream.read(buffer)) > 0) {
                gzos.write(buffer, 0, len);
            }

            gzos.finish();
        } catch (IOException e) {
            throw new GradleException("Unable to GZIP theme CSS", e);
        }
    }

    protected void performCssCompression(File themeDir, File cssFile) {
        getLogger().info("[ThemeCompile] compress theme '{}'", themeDir.getName());

        File compressedFile = new File(cssFile.getAbsolutePath() + ".compressed");

        try (FileReader cssReader = new FileReader(cssFile);
             Writer out = new BufferedWriter(new FileWriter(compressedFile))) {
            CssCompressor compressor = new CssCompressor(cssReader);
            compressor.compress(out, 0);
        } catch (IOException e) {
            throw new GradleException("Unable to minify CSS theme " + themeDir.getName(), e);
        }

        if (compressedFile.exists()) {
            try {
                FileUtils.forceDelete(cssFile);
            } catch (IOException e) {
                throw new GradleException("Unable to delete CSS file " + cssFile.getAbsolutePath(), e);
            }

            boolean renamed = compressedFile.renameTo(cssFile);
            if (!renamed) {
                throw new GradleException("Unable to move file " + cssFile.getAbsolutePath());
            }
        }
    }

    protected void prepareAppComponentsInclude(File themeBuildDir) {
        Configuration appComponentConf = getProject().getRootProject().getConfigurations().getByName("appComponent");
        if (appComponentConf.getDependencies().size() > 0) {
            prepareAppComponentsIncludeConfiguration(themeBuildDir);
        }
    }

    protected void prepareAppComponentsIncludeConfiguration(File themeBuildDir) {
        getLogger().info("[ThemeCompile] include styles from app components using Gradle configuration");

        File appComponentsIncludeFile = new File(themeBuildDir, "app-components.scss");
        if (appComponentsIncludeFile.exists()) {
            // can be completely overridden in project
            return;
        }

        StringBuilder appComponentsIncludeBuilder = new StringBuilder();
        appComponentsIncludeBuilder.append("/* This file is managed automatically and will be overwritten */\n\n");

        Configuration appComponentConf = getProject().getRootProject().getConfigurations().getByName("appComponent");
        ResolvedConfiguration resolvedConfiguration = appComponentConf.getResolvedConfiguration();
        Set<ResolvedDependency> dependencies = resolvedConfiguration.getFirstLevelModuleDependencies();

        Set<ResolvedArtifact> addedArtifacts = new HashSet<>();
        List<String> includeMixins = new ArrayList<>();
        Set<String> includedAddonsPaths = new HashSet<>();
        Set<File> scannedJars = new HashSet<>();

        walkDependenciesFromAppComponentsConfiguration(dependencies, addedArtifacts, artifact -> {
            try (JarFile jarFile = new JarFile(artifact.getFile())) {
                Manifest manifest = jarFile.getManifest();
                if (manifest == null || manifest.getMainAttributes() == null) {
                    return;
                }

                String compId = manifest.getMainAttributes().getValue("");
                String compVersion = manifest.getMainAttributes().getValue("");
                if (compId == null || compVersion == null) {
                    return;
                }

                getLogger().info("[ThemeCompile] include styles from app component {}", compId);

                File componentThemeDir = new File(themeBuildDir, compId);
                File addonsIncludeFile = new File(componentThemeDir, "vaadin-addons.scss");

                List<File> dependentJarFiles = new ArrayList<>(findDependentJarsByAppComponent(compId));
                dependentJarFiles.removeAll(scannedJars);
                scannedJars.addAll(dependentJarFiles);

                // ignore automatic lookup if defined file vaadin-addons.scss
                if (!addonsIncludeFile.exists()) {
                    findAndIncludeVaadinStyles(dependentJarFiles, includedAddonsPaths, includeMixins, appComponentsIncludeBuilder);
                } else {
                    getLogger().info("[ThemeCompile] ignore vaadin addon styles for {}", compId);
                }

                includeComponentScss(themeBuildDir, compId, appComponentsIncludeBuilder, includeMixins);
            } catch (IOException e) {
                throw new GradleException("Unable to form app-component includes for theme", e);
            }
        });

        for (String includeAppComponentId : includedAppComponentIds) {
            getLogger().info("[ThemeCompile] include styles from app component {}", includeAppComponentId);

            // autowiring of vaadin addons from includes is not supported
            includeComponentScss(themeBuildDir, includeAppComponentId, appComponentsIncludeBuilder, includeMixins);
        }

        appComponentsIncludeBuilder.append('\n');

        // include project includes and vaadin addons
        getLogger().info("[ThemeCompile] include styles from project and addons");

        String currentProjectId = getProject().getGroup().toString();
        File componentThemeDir = new File(themeBuildDir, currentProjectId);
        File addonsIncludeFile = new File(componentThemeDir, "vaadin-addons.scss");

        if (!addonsIncludeFile.exists()) {
            Configuration compileConfiguration = getProject().getConfigurations().getByName("compile");

            List<File> resolvedFiles = compileConfiguration.getResolvedConfiguration()
                    .getResolvedArtifacts().stream()
                    .map(ResolvedArtifact::getFile)
                    .filter(f -> f.exists() && f.getName().endsWith(".jar") && !scannedJars.contains(f))
                    .collect(Collectors.toList());

            findAndIncludeVaadinStyles(resolvedFiles, includedAddonsPaths, includeMixins,
                    appComponentsIncludeBuilder);
        } else {
            getLogger().info("[ThemeCompile] ignore vaadin addon styles for $currentProjectId");
        }

        // print mixins
        appComponentsIncludeBuilder.append("\n@mixin app_components {\n");
        for (String mixin : includeMixins) {
            appComponentsIncludeBuilder.append("  @include ").append(mixin).append(";\n");
        }
        appComponentsIncludeBuilder.append('}');

        try {
            FileUtils.write(appComponentsIncludeFile, appComponentsIncludeBuilder.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GradleException("Unable to write CSS theme includes", e);
        }

        getLogger().info("[ThemeCompile] app-components.scss initialized");
    }

    protected void walkDependenciesFromAppComponentsConfiguration(Set<ResolvedDependency> dependencies,
                                                                  Set<ResolvedArtifact> addedArtifacts,
                                                                  Consumer<ResolvedArtifact> artifactAction) {
        for (ResolvedDependency dependency : dependencies) {
            walkDependenciesFromAppComponentsConfiguration(dependency.getChildren(), addedArtifacts, artifactAction);

            for (ResolvedArtifact artifact : dependency.getModuleArtifacts()) {
                if (addedArtifacts.contains(artifact)) {
                    continue;
                }

                addedArtifacts.add(artifact);

                if (artifact.getFile().getName().endsWith(".jar")) {
                    artifactAction.accept(artifact);
                }
            }
        }
    }

    // find all dependencies of this app component
    protected List<File> findDependentJarsByAppComponent(String compId) {
        Configuration compileConfiguration = getProject().getConfigurations().getByName("compile");
        Set<ResolvedDependency> firstLevelModuleDependencies =
                compileConfiguration.getResolvedConfiguration().getFirstLevelModuleDependencies();

        return firstLevelModuleDependencies.stream()
                .flatMap(rd -> {
                    if (compId.equals(rd.getModuleGroup())) {
                        return rd.getAllModuleArtifacts().stream()
                                .filter(ra -> ra.getFile().exists() && ra.getFile().getName().equals(".jar"))
                                .map(ResolvedArtifact::getFile);
                    }

                    return Stream.empty();
                }).collect(Collectors.toList());
    }

    protected void includeComponentScss(File themeBuildDir, String componentId,
                                        StringBuilder appComponentsIncludeBuilder, List<String> includeMixins) {
        File componentThemeDir = new File(themeBuildDir, componentId);
        File componentIncludeFile = new File(componentThemeDir, "app-component.scss");

        if (componentIncludeFile.exists()) {
            appComponentsIncludeBuilder.append(
                    String.format("@import \"%s/%s\";\n", componentThemeDir.getName(), componentIncludeFile.getName())
            );

            includeMixins.add(componentId.replace('.', '_'));
        }
    }

    // find all vaadin addons in dependencies of this app component
    protected void findAndIncludeVaadinStyles(List<File> dependentJarFiles, Set<String> includedAddonsPaths,
                                              List<String> includeMixins, StringBuilder appComponentsIncludeBuilder) {
        for (File file : dependentJarFiles) {

            try (FileInputStream is = new FileInputStream(file);
                 JarInputStream jarStream = new JarInputStream(is)) {

                String vaadinStylesheets = getVaadinStylesheets(jarStream);
                if (vaadinStylesheets != null) {
                    includeVaadinStyles(vaadinStylesheets, includeMixins, includedAddonsPaths, appComponentsIncludeBuilder);
                }
            } catch (IOException e) {
                throw new GradleException("Unable to read SCSS theme includes", e);
            }
        }
    }

    protected void includeVaadinStyles(String vaadinStylesheets, List<String> includeMixins, Set<String> includedPaths,
                             StringBuilder appComponentsIncludeBuilder) {
        List<String> vAddonIncludes = Splitter.on(",").omitEmptyStrings().trimResults()
                                        .splitToList(vaadinStylesheets);

        for (String include : new LinkedHashSet<>(vAddonIncludes)) {
            if (!include.startsWith("/")) {
                include = '/' + include;
            }

            if (includedPaths.contains(include)) {
                continue;
            }

            includedPaths.add(include);

            getLogger().info("[ThemeCompile] include vaadin addons styles '{}'", include);

            if (include.endsWith(".css")) {
                appComponentsIncludeBuilder.append(String.format("@import url(\"../../..%s\");\n", include));
            } else {
                String mixin = include.substring(include.lastIndexOf("/") + 1,
                        include.length() - ".scss".length());

                appComponentsIncludeBuilder.append(String.format("@import \"../../..%s\";\n", include));

                includeMixins.add(mixin);
            }
        }
    }

    @Nullable
    protected String getVaadinStylesheets(JarInputStream jarStream) {
        Manifest mf = jarStream.getManifest();
        if (mf != null && mf.getMainAttributes() != null) {
            return mf.getMainAttributes().getValue(VAADIN_STYLESHEETS_MANIFEST_KEY);
        }
        return null;
    }

    protected void copyResources(File themesBuildDir, File themesDestDir) {
        getProject().copy(copySpec ->
                copySpec.from(themesBuildDir)
                        .into(themesDestDir)
                        .exclude(element -> {
                            String name = element.getFile().getName();
                            return name.startsWith(".") || name.endsWith(".scss");
                        }));
    }

    protected void removeEmptyDirs(File themesDestDir) {
        recursiveVisitDir(themesDestDir,  f -> {
            String[] list = f.list();
            if (list == null) {
                return;
            }

            if (list.length == 0) {
                Path relativePath = themesDestDir.toPath().relativize(f.toPath());

                getLogger().debug("[CubaWebScssThemeCreation] remove empty dir {} in '{}'", relativePath,
                        themesDestDir.getName());

                try {
                    FileUtils.deleteDirectory(f);
                } catch (IOException e) {
                    throw new GradleException("Unable to delete empty dir", e);
                }
            }
        });
    }

    protected void recursiveVisitDir(File dir, Consumer<File> apply) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File f : files) {
            if (f.exists() && f.isDirectory()) {
                recursiveVisitDir(f, apply);
                apply.accept(f);
            }
        }
    }
}