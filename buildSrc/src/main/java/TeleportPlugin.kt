import org.gradle.api.Plugin
import org.gradle.api.Project


open class TeleportPlugin : Plugin<Project> {


    companion object {
        const val DEFAULT_ENABLED = true
        const val DEFAULT_ATTEMPT_COUNT = 1
        const val DEFAULT_ATTEMPT_DURATION = 5000L
    }

    override fun apply(target: Project) {
        target.tasks.register("uploadApk", TelegramTask::class.java) {
            val extension = target.extensions.create("TeleportExtension", TeleportExtension::class.java)
            init(extension)
            it.apply {
                when (extension.applicationMode.get()) {
                    AppMode.DEBUG -> dependsOn("build")
                    AppMode.RELEASE -> dependsOn("build")
                }
                taskEnabled = extension.enabled.get()
                attemptCount = extension.attemptCount.get()
                attemptDuration = extension.attemptDuration.get()
            }
        }
    }

    private fun init(extension: TeleportExtension) {
        extension.enabled.convention(DEFAULT_ENABLED)
        extension.attemptCount.convention(DEFAULT_ATTEMPT_COUNT)
        extension.attemptDuration.convention(DEFAULT_ATTEMPT_DURATION)
        extension.applicationMode.convention(AppMode.RELEASE)
    }
}

