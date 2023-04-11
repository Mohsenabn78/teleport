plugins {
    kotlin("jvm") version "1.5.10"
    id("com.gradle.plugin-publish") version "1.2.0"
    id("org.gradle.java-gradle-plugin")
    id("org.jetbrains.dokka") version "1.5.0"
}

//apply<TeleportPlugin>()

group = "io.github.mohsenabn78.teleport"
version = "1.1.0"


gradlePlugin {
    plugins {
        create("plugin") {
            id = "io.github.mohsenabn78.teleport"
            implementationClass = "TeleportPlugin"
            displayName = "teleport plugin"
            description = " a plugin for send apk to telegram and other platform "
        }
    }
}

pluginBundle {
    website = "https://github.com/Mohsenabn78/teleport"
    vcsUrl = "https://github.com/Mohsenabn78/teleport.git"
    tags = listOf("telegram","deliver","apk","upload")

}


repositories {
    google()
    mavenCentral()
}


dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
    implementation("org.codehaus.groovy:groovy-all:3.0.5")
    implementation("io.github.resilience4j:resilience4j-retry:1.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
