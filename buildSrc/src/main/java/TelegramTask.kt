import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult
import java.io.File
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import java.time.Duration


open class TelegramTask : DefaultTask() {

    init {
        group = "deliver"
        enabled = true
    }

    @TaskAction
    fun sendApkToTelegram() {
        val retryConfig = RetryConfig.custom<Any>()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(5))
            .build()
        val retry = Retry.of("sendApkToTelegram", retryConfig)

        retry.executeSupplier<Any> {

            val jsonFile = File(project.projectDir.path + "/secret.json")
            if (!jsonFile.exists()) throw GradleException("secret.json file not found in ${jsonFile.path}")
            val parsedJson = JsonSlurper().parse(jsonFile) as Map<String, String>

            val botToken = parsedJson["botToken"]
            val chatId = parsedJson["chatID"]

            val apkFile = File(project.buildDir.path + "/outputs/apk/debug/app-debug.html")

            if (!apkFile.exists()) throw GradleException("apk file not found in ${apkFile.path}")

            val process = project.exec {
                it.commandLine(
                    "curl",
                    "-F", "document=@$apkFile",
                    "https://api.telegram.org/bot$botToken/sendDocument?chat_id=$chatId"
                )
            }

            logProcessOutput(process)

        }

    }


    private fun logProcessOutput(res: ExecResult) {
        println()
        if (res.exitValue == 0) {
            println("............. UPLOAD IS SUCCESSFUL ..............")
        } else {
            throw GradleException("upload process is failed! please check network")
        }
    }

}