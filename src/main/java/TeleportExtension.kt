import org.gradle.api.provider.Property

/**
 * extension for make plugin intractable
 * @param taskEnabled active or deActive task execution
 * @param attemptCount for set retry to task after fail running
 * @param attemptDuration for make a delay between retries
 * @param caption specifies a message for apk that want to sent
 * @param applicationName specifies a name for apk that want to sent
 * @param buildType build types -> debug , release
 **/
interface TeleportExtension {
    val enabled: Property<Boolean>
    val buildType: Property<ApplicationMode>
    val caption: Property<String>
    val applicationName: Property<String>
    val attemptCount: Property<Int>
    val attemptDuration: Property<Long>
}

sealed class ApplicationMode {
    object DEBUG : ApplicationMode()
    object RELEASE : ApplicationMode()
}