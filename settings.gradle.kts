plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "rentcar"
include(
    "commonmodels",
    "core",
    "domainservice",
    "gateway",
    "grpc-api",
    "internal-api"
)
include("domainservice:user")
include("domainservice:car")
include("domainservice:repairing")
include("domainservice:order")
include("domainservice:common")
include("domainservice:migration")
