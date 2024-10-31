plugins {
    id("kotlin-conventions")
    `java-test-fixtures`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")
}
