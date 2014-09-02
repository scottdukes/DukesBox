package net.scottdukes.gradle.plugins

import com.android.build.gradle.internal.LoggerWrapper
import com.android.build.gradle.internal.SdkHandler
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject

/**
 * Configures the Java plugin to use the Android boot class path.
 */
class JavaAndroidPlugin implements Plugin<Project> {
    private Instantiator instantiator
    private Project project

    @Inject
    JavaAndroidPlugin(Instantiator instantiator) {
        this.instantiator = instantiator
    }

    @Override
    void apply(Project project) {
        this.project = project

        project.plugins.apply(JavaPlugin)

        def android = project.extensions.create("android", SimpleAndroidExtension)

        project.afterEvaluate {
            configureCompileJava()
        }
    }

    protected void configureCompileJava() {
        def sdk = new SdkHandler(project, new LoggerWrapper(project.logger))
        def bootClasspath = "${sdk.sdkFolder}/platforms/${project.android.compileSdkVersion}/android.jar"
        project.logger.info "bootClasspath: $bootClasspath"

        project.tasks.withType(JavaCompile) { t ->
            t.options.bootClasspath = bootClasspath
        }
    }
}