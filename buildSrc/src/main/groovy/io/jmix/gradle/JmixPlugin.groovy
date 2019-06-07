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

import io.jmix.gradle.ui.ThemeCompile
import io.jmix.gradle.ui.WidgetsCompile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

class JmixPlugin implements Plugin<Project> {

    public static final String THEMES_CONFIGURATION_NAME = 'themes'
    public static final String WIDGETS_CONFIGURATION_NAME = 'widgets'

    public static final String COMPILE_THEMES_TASK_NAME = 'compileThemes'
    public static final String COMPILE_WIDGETS_TASK_NAME = 'compileWidgets'

    @Override
    void apply(Project project) {
        project.extensions.create("entitiesEnhancing", EnhancingExtension, project)

        project.afterEvaluate {
            if (project.entitiesEnhancing.enabled) {
                // todo find a better way to provide classpath for EclipseLink weaver
                project.dependencies.add('compile', 'org.eclipse.persistence:org.eclipse.persistence.jpa:2.7.3-1-cuba')

                project.tasks.findByName('compileJava').doLast(new EnhancingAction('main'))
                project.tasks.findByName('compileTestJava').doLast(new EnhancingAction('test'))
            }
        }

        project.ext.ThemeCompile = ThemeCompile.class
        Configuration themesConfiguration = project.configurations.create(THEMES_CONFIGURATION_NAME)

        def compileThemes = project.tasks.create(COMPILE_THEMES_TASK_NAME, ThemeCompile.class)
        compileThemes.enabled = false
        project.afterEvaluate {
            if (themesConfiguration.getDependencies().size() > 0) {
                project.sourceSets.main.output.dir(compileThemes.outputDirectory, builtBy: compileThemes)
                compileThemes.enabled = true
            }
        }

        project.ext.WidgetsCompile = WidgetsCompile.class
        Configuration widgetsConfiguration = project.configurations.create(WIDGETS_CONFIGURATION_NAME)

        def compileWidgetsTask = project.tasks.create(COMPILE_WIDGETS_TASK_NAME, WidgetsCompile.class)
        compileWidgetsTask.enabled = false
        project.afterEvaluate {
            if (widgetsConfiguration.size() > 0) {
                project.sourceSets.main.output.dir(compileWidgetsTask.outputDirectory, builtBy: compileWidgetsTask)
                compileWidgetsTask.enabled = true
            }
        }
    }
}