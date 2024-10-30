plugins {
    id("spring-conventions")
    `java-test-fixtures`
    id("org.jetbrains.kotlin.plugin.spring")
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":core"))
    implementation("io.nats:jnats:2.16.14")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    testImplementation("io.projectreactor:reactor-test:3.6.10")
    testImplementation("io.mockk:mockk:1.13.12")
    testFixturesImplementation(project(":internal-api"))
    testFixturesImplementation(project(":core"))
    testImplementation(testFixtures(project(":core")))
    testFixturesImplementation(testFixtures(project(":core")))
    testFixturesImplementation("org.mongodb:bson:5.0.1")
}
