import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *
 * A Gradle plugin for uploading APK files to Telegram.
 *
 *
 * This plugin registers a task called "uploadApk" for the current project, which can be used to upload the [APK file] to
 * a Telegram channel or group. The plugin uses a TeleportExtension object to configure the task parameters.
 *
 *
 * The following parameters can be configured using the [TeleportExtension] object:
 *
 *  * enabled: whether the [uploadApk task] is enabled or not.
 *  * attemptCount: the number of attempts to upload the APK file.
 *  * attemptDuration: the duration between attempts in milliseconds.
 *  * buildType: the [build type] (debug or release).
 *  * caption: the caption to be added to the uploaded APK file.
 *  * applicationName: the name of the application to be uploaded.
 *
 *
 * This plugin requires the following dependencies:
 *
 *  * org.telegram:telegrambots:5.1.1
 *  * org.jetbrains.kotlin:kotlin-stdlib-jdk8
 *
 * @version 1.0.0
 * @since 2023-04-11
 */
open class TeleportPlugin : Plugin<Project> {

    companion object {
        const val DEFAULT_ENABLED = true
        const val DEFAULT_ATTEMPT_COUNT = 1
        const val DEFAULT_ATTEMPT_DURATION = 5000L
        const val DEFAULT_APP_NAME = "app"
        const val DEFAULT_CAPTION = ""
        const val DEFAULT_TASK_NAME = "uploadApk"
        const val DEFAULT_EXTENSION_NAME = "TeleportExtension"
        const val DEFAULT_ASSEMBLE_DEBUG_TASK = "assembleDebug"
        const val DEFAULT_ASSEMBLE_RELEASE_TASK = "assembleRelease"
    }

    /**
     * Registers the "uploadApk" task for the current project and configures it using the TeleportExtension object.
     * @param target the project to register the task for
     */
    override fun apply(target: Project) {
        target.tasks.register(DEFAULT_TASK_NAME, TelegramTask::class.java) {
            val extension = target.extensions.create(DEFAULT_EXTENSION_NAME, TeleportExtension::class.java)
            init(extension)
            it.apply {
                when (extension.buildType.get()) {
                    ApplicationMode.DEBUG -> dependsOn(DEFAULT_ASSEMBLE_DEBUG_TASK)
                    ApplicationMode.RELEASE -> dependsOn(DEFAULT_ASSEMBLE_RELEASE_TASK)
                }
                taskEnabled = extension.enabled.get()
                attemptCount = extension.attemptCount.get()
                attemptDuration = extension.attemptDuration.get()
                buildType = extension.buildType.get()
                caption = extension.caption.get()
                applicationName = extension.applicationName.get()
            }
        }
    }

    /** adjust the convention for conditions where no value has been sent to the extension  **/
    private fun init(extension: TeleportExtension) {
        extension.enabled.convention(DEFAULT_ENABLED)
        extension.attemptCount.convention(DEFAULT_ATTEMPT_COUNT)
        extension.attemptDuration.convention(DEFAULT_ATTEMPT_DURATION)
        extension.buildType.convention(ApplicationMode.RELEASE)
        extension.caption.convention(DEFAULT_CAPTION)
        extension.applicationName.convention(DEFAULT_APP_NAME)
    }
}

