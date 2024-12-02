plugins {
    id("spring-conventions")
    id("grpc-conventions")
    `java-test-fixtures`
}

dependencies {
    implementation(project(":core"))
    implementation(project(":grpc-api"))
    implementation("systems.ajax:nats-spring-boot-starter:4.1.0.186.MASTER-SNAPSHOT")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    testImplementation("io.projectreactor:reactor-test:3.6.10")
    testImplementation("io.mockk:mockk:1.13.12")
}
