plugins {
    id("kotlin-conventions")
    `java-test-fixtures`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    testFixturesImplementation("org.mongodb:bson:5.0.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")
}
