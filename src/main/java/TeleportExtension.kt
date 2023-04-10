import org.gradle.api.provider.Property

interface TeleportExtension {
    val enabled: Property<Boolean>
    val applicationMode: Property<AppMode>
    val attemptCount: Property<Int>
    val attemptDuration: Property<Long>
}

sealed class AppMode {
    object DEBUG : AppMode()
    object RELEASE : AppMode()
}