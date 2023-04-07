import org.gradle.api.Plugin
import org.gradle.api.Project


class TelegramPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tasks.register("sendApkToTelegram",TelegramTask::class.java){
            it.dependsOn("build")
        }
    }

}