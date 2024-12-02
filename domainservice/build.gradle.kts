plugins {
    id("spring-conventions")
    `java-test-fixtures`
}

dependencies {
    implementation(project(":domainservice:common"))
    implementation(project(":commonmodels"))
    implementation(project(":domainservice:car"))
    implementation(project(":domainservice:user"))
    implementation(project(":domainservice:order"))
    implementation(project(":domainservice:repairing"))
    implementation(project(":core"))
    implementation(project(":internal-api"))
    implementation("systems.ajax:nats-spring-boot-starter:4.1.0.186.MASTER-SNAPSHOT")
    implementation("systems.ajax:kafka-spring-boot-starter:3.0.3.170.MASTER-SNAPSHOT")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.projectreactor.kafka:reactor-kafka")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("io.mongock:mongock-springboot-v3:5.4.4")
    implementation("io.mongock:mongodb-springdata-v4-driver:5.4.4")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    testImplementation("systems.ajax:kafka-mock:3.0.3.170.MASTER-SNAPSHOT")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:mongodb:1.19.0")
    testImplementation("io.projectreactor:reactor-test:3.6.10")
    testImplementation("io.mockk:mockk:1.13.12")
}
