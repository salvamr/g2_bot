plugins {
    kotlin("jvm") version "1.6.20"
    id("org.bytedeco.gradle-javacpp-platform").version("1.5.7")
    application
}

group = "me.pawer"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("com.fazecast:jSerialComm:2.9.1")
    implementation("net.java.dev.jna:jna:5.11.0")

    implementation("org.bytedeco:opencv-platform:4.5.5-1.5.7")
    implementation("org.bytedeco:opencv-platform-gpu:4.5.5-1.5.7")
    implementation("org.bytedeco:mkl-platform:2022.0-1.5.7")

    implementation("org.bytedeco:cuda-platform:11.6-8.3-1.5.7")
    implementation("org.bytedeco:cuda-platform-redist:11.6-8.3-1.5.7")
}

application {
    mainClass.set("MainKt")
}
