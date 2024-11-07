plugins {
    id("kotlin-conventions")
    id("delta-coverage-conventions")
}

allprojects {
    group = "com.makarytskyi"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.check {
    dependsOn(tasks.deltaCoverage)
}
