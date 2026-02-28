import arrow.optics.plugin.arrowOptics
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val api_version: String by project

plugins {
    idea
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    id("io.arrow-kt.optics") version "2.2.1.1"
}

version = api_version

base {
    archivesName.set("spark-core-api")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.8.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.4.0")
    implementation("io.arrow-kt:arrow-core:2.2.1.1")
    implementation("io.arrow-kt:arrow-core-serialization:2.2.1.1")
    implementation("io.arrow-kt:arrow-fx-coroutines:2.2.1.1")
    implementation("io.arrow-kt:arrow-optics:2.2.1.1")
    implementation("io.github.quillraven.fleks:Fleks-jvm:2.12")
    implementation("org.graalvm.polyglot:polyglot:24.2.1")
    implementation("org.graalvm.polyglot:js:24.2.1")
    implementation("org.slf4j:slf4j-api:2.0.17")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xcontext-parameters"))
}

kotlin {
    arrowOptics()
}