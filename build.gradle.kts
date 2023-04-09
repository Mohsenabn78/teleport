plugins {
    kotlin("jvm") version "1.5.10"
    groovy
    java
    id("com.gradle.plugin-publish") version "1.2.0"
    id("org.gradle.java-gradle-plugin")
}

apply<TeleportPlugin>()

group = "io.mohsen.teleport"
version = "1.0"


gradlePlugin {
    plugins {
        create("plugin") {
            id = "io.mohsen.teleport"
            implementationClass = "TelegramPlugin"
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
