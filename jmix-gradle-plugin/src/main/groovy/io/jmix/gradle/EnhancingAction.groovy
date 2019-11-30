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
import javassist.bytecode.AnnotationsAttribute
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task

class EnhancingAction implements Action<Task> {

    private String sourceSetName

    EnhancingAction(String sourceSetName) {
        this.sourceSetName = sourceSetName
    }

    @Override
    void execute(Task task) {
        Project project = task.getProject()
        project.logger.warn "Enhancing entities in $project for source set '$sourceSetName'"

        def sourceSet = project.sourceSets.findByName(sourceSetName)
        def srcDirs = sourceSet.allJava.getSrcDirs()

        ClassPool classPool = new ClassPool(null)
        classPool.appendSystemPath()
        classPool.insertClassPath(sourceSet.java.outputDir.absolutePath)

        List<String> classNames = []
        List<String> nonMappedClassNames = []

        srcDirs.each { File srcDir ->
            project.fileTree(srcDir).each { File file ->
                if (file.name.endsWith('.java')) {
                    String pathStr = srcDir.toPath().relativize(file.toPath()).join('.')
                    String className = pathStr.substring(0, pathStr.length() - '.java'.length())

                    CtClass ctClass = null
                    try {
                        ctClass = classPool.get(className)
                    } catch (NotFoundException e) {
                        project.logger.warn "Entity $className for enhancing is not found in $project"
                    }

                    if (ctClass != null) {
                        AnnotationsAttribute attribute =
                                ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag)
                        if (attribute != null) {
                            if (attribute.getAnnotation("javax.persistence.Entity") != null
                                    || attribute.getAnnotation("javax.persistence.MappedSuperclass") != null
                                    || attribute.getAnnotation("javax.persistence.Embeddable") != null) {
                                project.logger.warn "Entity $className for enhancing in $project"
                                classNames.add(className)
                            } else if (attribute.getAnnotation("io.jmix.core.metamodel.annotations.MetaClass") != null) {
                                nonMappedClassNames.add(className)
                            }
                        }
                    }
                }
            }
        }

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

        List<String> allClassNames = classNames + nonMappedClassNames
        if (!allClassNames.isEmpty()) {
            project.logger.info "[JmixEnhancer] Start Jmix enhancing"

            ClassPool pool = new ClassPool(null)
            pool.appendSystemPath()

            for (file in sourceSet.compileClasspath) {
                pool.insertClassPath(file.getAbsolutePath())
            }

            String javaOutputDir = sourceSet.java.outputDir.absolutePath

            pool.insertClassPath(javaOutputDir)
            project.configurations.enhancing.files.each { File dep ->
                pool.insertClassPath(dep.absolutePath)
            }

            def cubaEnhancer = new JmixEnhancer(pool, sourceSet.java.outputDir.absolutePath)
            cubaEnhancer.logger = project.logger

            for (className in allClassNames) {
                def classFileName = className.replace('.', '/') + '.class'
                def classFile = new File(javaOutputDir, classFileName)

                if (classFile.exists()) {
                    // skip files from dependencies, enhance only classes from `javaOutputDir`
                    cubaEnhancer.run(className)
                }
            }
        }
    }
}
