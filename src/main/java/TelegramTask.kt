import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.gradle.api.tasks.Input
import java.io.ByteArrayOutputStream
import java.time.Duration

/**
 * A Gradle task for uploading APK files to Telegram.
 *
 * This task uploads the APK file to a Telegram channel or group. The task uses a retry mechanism from the
 * Resilience4j library and a JsonSlurper for parsing the secret token and chat ID from a JSON file.
 *
 * The following parameters can be configured for this task:
 *
 * * taskEnabled: whether the task is enabled or not.
 * * attemptCount: the number of attempts to upload the APK file.
 * * attemptDuration: the duration between attempts in milliseconds.
 * * caption: the caption to be added to the uploaded APK file.
 * * applicationName: the name of the application to be uploaded.
 * * buildType: the build type (debug or release).
 *
 * This task requires the following dependencies:
 *
 * * groovy.json.JsonSlurper
 * * org.gradle.process.ExecResult
 * * io.github.resilience4j.retry.Retry
 *
 *
 * @version 1.0.0
 * @since 2023-04-11
 */
open class TelegramTask : DefaultTask() {

    /** Whether the task is enabled or not.*/
    @Input
    var taskEnabled: Boolean = DEFAULT_ENABLED

    /** The number of attempts to upload the APK file.*/
    @Input
    var attemptCount: Int = DEFAULT_ATTEMPT_COUNT

    /** The duration between attempts in milliseconds.*/
    @Input
    var attemptDuration: Long = DEFAULT_ATTEMPT_DURATION

    /** The caption to be added to the uploaded APK file.*/
    @Input
    var caption: String = DEFAULT_CAPTION

    /** The name of the application to be uploaded.*/
    @Input
    var applicationName: String = DEFAULT_APP_NAME

    /** The build type (debug or release).*/
    @Input
    var buildType: ApplicationMode = ApplicationMode.RELEASE

    companion object {
        const val DEFAULT_ENABLED = true
        const val DEFAULT_TASK_GROUP = "deliver"
        const val DEFAULT_ATTEMPT_COUNT = 1
        const val DEFAULT_ATTEMPT_DURATION = 5000L
        const val SIGNED_APK_NAME = "app-release-signed.apk"
        const val UNSIGNED_APK_NAME = "app-release-unsigned.apk"
        const val DEFAULT_CAPTION = ""
        const val DEFAULT_APP_NAME = "app"
    }

    init {
        group = DEFAULT_TASK_GROUP
        enabled = taskEnabled
    }


    @TaskAction
    fun uploadApk() {
        // Retry mechanism from Resilience4j library
        val retry = makeRetryHandler()
        retry.executeSupplier<Any> {

            // Parse the secret token and chat ID from a JSON file
            val secretElements = readeSecretTokenAndId()
            val botToken = secretElements.first
            val chatId = secretElements.second
            // Get the APK file and its version
            val apkName = makeApkName()
            val apkPath = makeApkPath(apkName)
            val apkFile = File(project.buildDir.path + apkPath)

            val applicationVersion = getApkVersion(apkFile)
            // Rename the APK file with the [version number]
            val renamedApkName = makeRenamedApkName(applicationVersion)
            val renamedApkPath = makeRenamedApkPath(renamedApkName)
            val newApkFile = File(project.buildDir.path + renamedApkPath)

            apkFile.renameTo(newApkFile)

            project.exec {
                it.commandLine(
                    "curl",
                    "-F", "document=@$newApkFile",
                    "-F", "caption=$caption",
                    "https://api.telegram.org/bot$botToken/sendDocument?chat_id=$chatId"
                )
            }

        }

    }


    private fun readeSecretTokenAndId(): Pair<String, String> {
        val jsonFile = File(project.projectDir.path + "/secret.json")
        if (!jsonFile.exists()) throw GradleException("secret.json file not found in ${jsonFile.path}")
        val parsedJson = JsonSlurper().parse(jsonFile) as Map<String, String>
        return Pair(parsedJson["botToken"]!!, parsedJson["chatID"]!!)
    }


    private fun makeRetryHandler(): Retry {
        val retryConfig = RetryConfig.custom<Any>()
            .maxAttempts(attemptCount)
            .waitDuration(Duration.ofMillis(attemptDuration))
            .build()
        return Retry.of("uploadApk", retryConfig)
    }

    private fun makeApkName(): String {
        return when (buildType) {
            ApplicationMode.DEBUG -> "app-debug.apk"
            ApplicationMode.RELEASE -> if (isApkInSignMode()) SIGNED_APK_NAME else UNSIGNED_APK_NAME
        }
    }

    private fun makeApkPath(apkName: String): String {
        return when (buildType) {
            ApplicationMode.DEBUG -> "/outputs/apk/debug/${apkName}"
            ApplicationMode.RELEASE -> "/outputs/apk/release/${apkName}"
        }
    }

    private fun makeRenamedApkName(appVersion: String): String {
        return when (buildType) {
            ApplicationMode.DEBUG -> "${applicationName}-debug-v${appVersion}.apk"
            ApplicationMode.RELEASE -> "${applicationName}-release-v${appVersion}.apk"
        }
    }

    private fun makeRenamedApkPath(renamedApkName: String): String {
        return when (buildType) {
            ApplicationMode.DEBUG -> "/outputs/apk/debug/${renamedApkName}"
            ApplicationMode.RELEASE -> "/outputs/apk/release/${renamedApkName}"
        }
    }

    private fun isApkInSignMode(): Boolean {
        return File(project.buildDir.path + "/outputs/apk/release/app-release-signed.apk").exists()
    }


    private fun getApkVersion(apkFile: File): String {
        if (!apkFile.exists()) throw GradleException("apk file not found in ${apkFile.path}")
        val aaptCommand = "aapt dump badging ${apkFile.absolutePath}"
        val aaptOutput = ByteArrayOutputStream()
        project.exec {
            it.commandLine("bash", "-c", "$aaptCommand 2>&1")
            it.standardOutput = aaptOutput
        }
        val manifestContent = aaptOutput.toString()
        return manifestContent.substringAfter("versionName='").substringBefore("'")
    }

}