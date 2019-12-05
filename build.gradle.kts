import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("jvm") version "1.3.50"
}

group = "cursLangMachine"
version = "1.0"

repositories {
    mavenCentral()
}

val os = OperatingSystem.current()!!
val platform = when { os.isWindows -> "win"; os.isLinux-> "linux"; os.isMacOsX -> "mac"; else -> error("Unknown OS") }

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation ("no.tornado:tornadofx:1.7.17")
    implementation("org.openjfx:javafx-fxml:11:$platform")
    implementation("org.openjfx:javafx-web:11:$platform")
    implementation("org.openjfx:javafx-media:11:$platform")
    implementation("org.openjfx:javafx-swing:11:$platform")
    implementation("org.openjfx:javafx-base:11:$platform")
    implementation("org.openjfx:javafx-graphics:11:$platform")
    implementation("org.openjfx:javafx-controls:11:$platform")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.github.mifmif:generex:1.0.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}