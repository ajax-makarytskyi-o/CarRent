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

configurations {
    named("testFixturesImplementation") {
        extendsFrom(configurations.implementation.get())
    }
}
