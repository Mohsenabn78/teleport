import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult
import java.io.File
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.gradle.api.tasks.Input
import java.time.Duration


open class TelegramTask : DefaultTask() {

    @Input
    var taskEnabled: Boolean = true

    @Input
    var attemptCount: Int = 1

    @Input
    var attemptDuration: Long = 100

    init {
        group = "deliver"
        enabled = taskEnabled
    }

    @TaskAction
    fun uploadApk() {
        val retry = makeRetryHandler()
        retry.executeSupplier<Any> {

            val jsonFile = File(project.projectDir.path + "/secret.json")
            if (!jsonFile.exists()) throw GradleException("secret.json file not found in ${jsonFile.path}")
            val parsedJson = JsonSlurper().parse(jsonFile) as Map<String, String>

            val botToken = parsedJson["botToken"]
            val chatId = parsedJson["chatID"]

            val apkFile = File(project.buildDir.path + "/outputs/apk/debug/app-debug.html")

            if (!apkFile.exists()) throw GradleException("apk file not found in ${apkFile.path}")

            project.exec {
                it.commandLine(
                    "curl",
                    "-F", "document=@$apkFile",
                    "https://api.telegram.org/bot$botToken/sendDocument?chat_id=$chatId"
                )
            }

        }

    }

    private fun makeRetryHandler(): Retry {
        val retryConfig = RetryConfig.custom<Any>()
            .maxAttempts(attemptCount)
            .waitDuration(Duration.ofMillis(attemptDuration))
            .build()
        return Retry.of("uploadApk", retryConfig)
    }

}