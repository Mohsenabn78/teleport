plugins {
    kotlin("jvm") version "1.5.10"
    groovy
    java
    id("maven-publish")
    id("java-gradle-plugin")
}

group = "com.mohsen"
version = "1.0-SNAPSHOT"



publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

gradlePlugin {
    plugins {
        create("telegram-plugin") {
            id = "com.mohsen.teleport"
            implementationClass = "TelegramPlugin"
        }
    }
}


publishing {
    publications {
        create<MavenPublication>("plugin") {
            groupId = "com.mohsen"
            artifactId = "teleport"
            version = "1.0.0"
            from(components["java"])
            artifact(tasks.named("jar"))
            pom {
                name.set("Telegram Plugin")
                description.set("A Gradle plugin for sending apk to Telegram")
                url.set("https://github.com/example/telegram-plugin")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("mohsenAbn78")
                        name.set("mohsenAbedini")
                        email.set("mohsenabedini79ooo@gmail.com")
                    }
                }
            }
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
    implementation("org.codehaus.groovy:groovy-all:3.0.5")
    implementation("io.github.resilience4j:resilience4j-retry:1.5.0")
}