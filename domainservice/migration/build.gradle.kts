plugins {
    id("spring-conventions")
}

group = "com.makarytskyi"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domainservice:car"))
    implementation(project(":domainservice:user"))
    implementation(project(":domainservice:order"))
    implementation(project(":domainservice:repairing"))
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("io.mongock:mongock-springboot-v3:5.4.4")
    implementation("io.mongock:mongodb-springdata-v4-driver:5.4.4")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
}

tasks.bootJar {
    enabled = false
}
