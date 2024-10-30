plugins {
    id("spring-conventions")
    `java-test-fixtures`
    id("org.jetbrains.kotlin.plugin.spring")
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":core"))
    implementation("io.nats:jnats:2.16.14")
    implementation("com.google.protobuf:protobuf-java:3.24.3")
    implementation("io.mongock:mongock-springboot-v3:5.4.4")
    implementation("io.mongock:mongodb-springdata-v4-driver:5.4.4")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation("org.testcontainers:mongodb:1.19.0")
    testImplementation("io.projectreactor:reactor-test:3.6.10")
    testImplementation("io.mockk:mockk:1.13.12")
    testFixturesImplementation(project(":internal-api"))
    testFixturesImplementation(project(":core"))
    testFixturesImplementation("org.mongodb:bson:5.0.1")
}