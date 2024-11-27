plugins {
    id("spring-conventions")
    `java-test-fixtures`
}

dependencies {
    implementation(project(":internal-api"))
    implementation(project(":commonmodels"))
    implementation(project(":core"))
    implementation("io.nats:jnats:2.16.14")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.projectreactor.kafka:reactor-kafka")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("io.mongock:mongock-springboot-v3:5.4.4")
    implementation("io.mongock:mongodb-springdata-v4-driver:5.4.4")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:mongodb:1.19.0")
    testImplementation("io.projectreactor:reactor-test:3.6.10")
    testImplementation("io.mockk:mockk:1.13.12")
}
