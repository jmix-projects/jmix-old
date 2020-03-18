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

package io.jmix.gradle

import groovy.xml.MarkupBuilder
import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task

import static io.jmix.gradle.MetaModelUtil.*

class EnhancingAction implements Action<Task> {

    private String sourceSetName

    EnhancingAction(String sourceSetName) {
        this.sourceSetName = sourceSetName
    }

    @Override
    void execute(Task task) {
        Project project = task.getProject()

        project.logger.warn "Enhancing entities in $project for source set '$sourceSetName'"

        List<String> classNames = []
        List<String> nonMappedClassNames = []
        def sourceSet = project.sourceSets.findByName(sourceSetName)

        generateEntityClassesList(project, sourceSet, classNames, nonMappedClassNames)

        runEclipseLinkEnhancing(project, classNames, sourceSet)

        runJmixEnhancing(project, classNames + nonMappedClassNames, sourceSet)
    }

    protected void generateEntityClassesList(Project project, sourceSet, classNames, nonMappedClassNames) {
        ClassPool classPool = new ClassPool(null)
        classPool.appendSystemPath()
        classPool.insertClassPath(sourceSet.java.outputDir.absolutePath)

        sourceSet.allJava.getSrcDirs().each { File srcDir ->
            project.fileTree(srcDir).each { File file ->
                if (file.name.endsWith('.java')) {
                    String pathStr = srcDir.toPath().relativize(file.toPath()).join('.')
                    String className = pathStr.substring(0, pathStr.length() - '.java'.length())

                    CtClass ctClass = null
                    try {
                        ctClass = classPool.get(className)
                    } catch (NotFoundException e) {
                        project.logger.info "Entity $className for enhancing is not found in $project"
                    }

                    if (ctClass != null) {
                        if (isJpaEntity(ctClass) || isJpaMappedSuperclass(ctClass) || isJpaEmbeddable(ctClass)) {
                            project.logger.warn "Entity $className for enhancing in $project"
                            classNames.add(className)
                        } else if (isMetaClass(ctClass)) {
                            nonMappedClassNames.add(className)
                        }
                    }
                }
            }
        }
    }

    protected void runEclipseLinkEnhancing(Project project, List<String> classNames, sourceSet) {
        if (!classNames.isEmpty()) {
            File file = new File(project.buildDir, "dummy/enhancing/$sourceSetName/META-INF/persistence.xml")
            file.parentFile.mkdirs()
            file.withWriter { writer ->
                def xml = new MarkupBuilder(writer)
                xml.persistence(version: '2', xmlns: 'http://java.sun.com/xml/ns/persistence') {
                    'persistence-unit'(name: 'jmix') {
                        classNames.each { String name ->
                            'class'(name)
                        }
                        'exclude-unlisted-classes'()
                        'properties'() {
                            'property'(name: 'eclipselink.weaving', value: 'static')
                        }
                    }
                }
            }

            project.javaexec {
                main = 'org.eclipse.persistence.tools.weaving.jpa.StaticWeave'
                classpath(
                        project.configurations.enhancing.asFileTree.asPath,
                        sourceSet.compileClasspath,
                        sourceSet.java.outputDir
                )
                args "-loglevel"
                args "INFO"
                args "-persistenceinfo"
                args "${project.buildDir}/dummy/enhancing/$sourceSetName"
                args sourceSet.java.outputDir.absolutePath
                args sourceSet.java.outputDir.absolutePath
                debug = project.hasProperty("debugEnhancing") ? Boolean.valueOf(project.getProperty("debugEnhancing")) : false
            }
        }
    }

    protected void runJmixEnhancing(Project project, List<String> classNames, sourceSet) {
        if (!classNames.isEmpty()) {
            project.logger.info "[JmixEnhancer] Start Jmix enhancing..."

            String javaOutputDir = sourceSet.java.outputDir.absolutePath

            for (EnhancingStep step : enhancingSteps()) {

                ClassPool classPool = new ClassPool(null)
                classPool.appendSystemPath()

                for (File file in sourceSet.compileClasspath) {
                    classPool.insertClassPath(file.getAbsolutePath())
                }

                classPool.insertClassPath(javaOutputDir)

                project.configurations.enhancing.files.each { File dep ->
                    classPool.insertClassPath(dep.absolutePath)
                }

                step.classPool = classPool
                step.outputDir = javaOutputDir
                step.logger = project.logger

                for (className in classNames) {
                    def classFileName = className.replace('.', '/') + '.class'
                    def classFile = new File(javaOutputDir, classFileName)

                    if (classFile.exists()) {
                        // skip files from dependencies, enhance only classes from `javaOutputDir`
                        step.execute(className)
                    }
                }
            }
        }
    }

    protected static List<EnhancingStep> enhancingSteps() {
        Arrays.asList(new EntityEntryEnhancingStep(), new SettersEnhancingStep(), new TransientAnnotationEnhancingStep())
    }
}
