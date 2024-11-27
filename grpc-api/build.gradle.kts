import com.google.protobuf.gradle.id

plugins {
    id("kotlin-conventions")
    id("grpc-conventions")
    id("com.google.protobuf") version "0.9.4"
}

dependencies {
    api("com.google.protobuf:protobuf-java:3.24.3")
    api(project(":commonmodels"))
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.3"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.59.0"
        }

        id("reactor-grpc") {
            artifact = "com.salesforce.servicelibs:reactor-grpc:1.2.4"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("reactor-grpc")
            }
        }
    }
}
