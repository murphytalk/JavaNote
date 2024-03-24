import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val projectVersion = "1.0"

plugins {
    id("java")
    kotlin("jvm") version "1.9.23"
    // java plugin is implicit
}


repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
//    jcenter()
    mavenCentral()
}

val sparkjava_version = "2.7.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    // Simon metrics
    implementation("org.javasimon:javasimon-core:4.1.2")

    // log
    implementation("ch.qos.logback:logback-classic:1.2.3")

    //  The followings are for the arithmetic console and web app
    /// sparkjava web framework
    implementation("com.sparkjava:spark-core:$sparkjava_version")
    implementation("com.sparkjava:spark-template-jinjava:$sparkjava_version")

    /// ANSI color
    implementation("org.fusesource.jansi:jansi:1.16")
    //
    implementation("com.google.code.gson:gson:2.8.2")
    implementation("org.codejargon:fluentjdbc:1.8.3")
    implementation("org.xerial:sqlite-jdbc:3.21.0.1")

    // For unit test
    //

    // Use the Kotlin test library.
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform() // JUnitPlatform for tests
}

kotlin { // Extension for easy setup
    jvmToolchain(21)
}

tasks {
    // the followings are for standard test task
    test {
        useJUnit()
    }
}


tasks.register("listrepos"){
    println("Repositories:")
    project.repositories.forEach { println ("Name:  ${it.name} ; ") }
}


// reference
// https://docs.gradle.org/current/userguide/kotlin_dsl.html
// good gradle kt build sample
// https://github.com/JetBrains/kotlin/blob/master/build.gradle.kts
