import gradle.kotlin.dsl.accessors._a6e2d5db45668b67b380a3a812f3c826.testImplementation
import gradle.kotlin.dsl.accessors._a6e2d5db45668b67b380a3a812f3c826.testRuntimeOnly
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    kotlin("jvm")
    jacoco
    `java-test-fixtures`
    id("io.gitlab.arturbosch.detekt")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

detekt {
    buildUponDefaultConfig = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        sarif.required.set(true)
        md.required.set(true)
    }
}
