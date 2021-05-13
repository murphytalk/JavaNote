import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val projectVersion = "1.0"

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.4.20"
    // java plugin is implicit
    id("com.adarshr.test-logger") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}


repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
    mavenCentral()
}

val sparkjava_version = "2.7.1"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

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
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs = listOf("-Xlint:unchecked")
}

tasks {
    // the followings are for standard test task
    test {
        testLogging.showExceptions = true
        testLogging.showStandardStreams = true
    }
    // the followings are used when test-logger plugin is used
    testlogger {
        //theme = "standard"
        showExceptions = true
        showStackTraces = true
        showFullStackTraces = false
        showCauses = true
        slowThreshold = 2000
        showSummary = true
        showSimpleNames = false
        showPassed = true
        showSkipped = true
        showFailed = true
        showStandardStreams = true
        showPassedStandardStreams = true
        showSkippedStandardStreams = true
        showFailedStandardStreams = true
    }

    shadowJar{
        archiveBaseName.set("JavaNote")
        archiveVersion.set(projectVersion)
        exclude ("**/test/**")
    }
    build {
        dependsOn(shadowJar)
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
