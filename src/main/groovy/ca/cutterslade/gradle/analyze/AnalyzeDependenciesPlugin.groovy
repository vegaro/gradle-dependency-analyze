package ca.cutterslade.gradle.analyze

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet

import java.util.concurrent.ConcurrentHashMap

class AnalyzeDependenciesPlugin implements Plugin<Project> {
    @Override
    void apply(final Project project) {
        if (project.rootProject == project) {
            project.rootProject.extensions.add(ProjectDependencyResolver.CACHE_NAME, new ConcurrentHashMap<>())
        }

        project.configurations.create('permitUnusedDeclared')
        project.configurations.create('permitTestUnusedDeclared')

        project.tasks.create('analyzeClassesDependencies',
                dependsOn: 'classes',
                type: AnalyzeDependenciesTask,
                group: 'Verification',
                description: 'Analyze project for dependency issues related to main source set.'
        ) {
            require = [
                    project.configurations.compile,
                    project.configurations.findByName('compileOnly'),
                    project.configurations.findByName('provided')
            ]
            allowedToDeclare = [
                    project.configurations.permitUnusedDeclared
            ]
            def output = project.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).output
            classesDirs = output.hasProperty('classesDirs') ? output.classesDirs : project.files(output.classesDir)
        }

    }
}
