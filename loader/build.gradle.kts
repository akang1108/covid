version = 1.0

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.61"
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.+")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.+")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClassName = "info.akang.covid.DataETLKt"
}
