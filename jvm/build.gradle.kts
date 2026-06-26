import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val api_version: String by project
val arrow_version: String by project
val graaljs_version: String by project

plugins {
    idea
    kotlin("jvm") apply true
}

version = api_version

base {
    archivesName.set("spark-core-api")
}

kotlin {
    jvmToolchain(25)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.8.0")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xcontext-parameters"))
}