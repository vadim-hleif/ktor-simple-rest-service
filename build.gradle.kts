import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.4.0"
}

dependencies {
    // BOMs
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.4.0"))
    testImplementation(platform("org.junit:junit-bom:5.7.0"))

    // kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    // Tests tools
    testImplementation("org.junit.jupiter:junit-jupiter")

    val kotestVersion = "4.3.1"
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")

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