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
import javassist.bytecode.AnnotationsAttribute
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task

class EnhancingAction implements Action<Task> {

    @Override
    void execute(Task task) {
        Project project = task.getProject()
        project.logger.warn "Enhancing entities in $project"

        def srcDirs = project.sourceSets.main.allJava.getSrcDirs()

        ClassPool classPool = new ClassPool(null)
        classPool.appendSystemPath()
        classPool.insertClassPath(project.sourceSets.main.java.outputDir.absolutePath)

        List<String> classNames = []
        srcDirs.each { File srcDir ->
            project.fileTree(srcDir).each { File file ->
                if (file.name.endsWith('.java')) {
                    String pathStr = srcDir.toPath().relativize(file.toPath()).join('.')
                    String className = pathStr.substring(0, pathStr.length() - '.java'.length())

                    CtClass ctClass = classPool.get(className)
                    AnnotationsAttribute attribute =
                            ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag)
                    if (attribute != null) {
                        if (attribute.getAnnotation("javax.persistence.Entity") != null
                                || attribute.getAnnotation("javax.persistence.MappedSuperclass") != null
                                || attribute.getAnnotation("javax.persistence.Embeddable") != null) {
                            classNames.add(className)
                        }
                    }
                }
            }
        }

        if (!classNames.isEmpty()) {
            File file = new File(project.buildDir, 'tmp/enhancing/META-INF/persistence.xml')
            file.parentFile.mkdirs()
            file.withWriter { writer ->
                def xml = new MarkupBuilder(writer)
                xml.persistence(version: '2', xmlns: 'http://java.sun.com/xml/ns/persistence') {
                    'persistence-unit'(name: 'jmix') {
                        classNames.each { String name ->
                            'class'(name)
                        }
                        'exclude-unlisted-classes'()
                    }
                }
            }

            project.javaexec {
                main = 'org.eclipse.persistence.tools.weaving.jpa.StaticWeave'
                classpath(
                        project.sourceSets.main.compileClasspath,
                        project.sourceSets.main.java.outputDir
                )
                args "-loglevel"
                args "INFO"
                args "-persistenceinfo"
                args "${project.buildDir}/tmp/enhancing"
                args project.sourceSets.main.java.outputDir.absolutePath
                args project.sourceSets.main.java.outputDir.absolutePath
                debug = project.hasProperty("debugEnhancing") ? Boolean.valueOf(project.getProperty("debugEnhancing")) : false
            }

        }
    }
}
