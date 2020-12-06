import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.4.0"
}

dependencies {
    /**
     * BOMs
     */
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    testImplementation(platform("org.junit:junit-bom:5.7.0"))

    /**
     * Kotlin
     */
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    /**
     * Ktor
     */
    val ktorVersion = "1.4.0"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    // json
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.0")
    // logging
    val logbackVersion = "1.2.1"
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    /**
     * Utils
     */
    // In-memory cache
    implementation("com.github.ben-manes.caffeine:caffeine:2.8.5")

    /**
     * Testing
     */
    testImplementation("org.junit.jupiter:junit-jupiter")
    // nice asserts
    val kotestVersion = "4.3.1"
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    // mocking
    testImplementation("io.mockk:mockk:1.10.3")
}

// Kotlin configs
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}

// JUnit 5
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}