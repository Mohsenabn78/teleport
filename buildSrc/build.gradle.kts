plugins {
    kotlin("jvm") version "1.5.10"
    groovy
    java
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