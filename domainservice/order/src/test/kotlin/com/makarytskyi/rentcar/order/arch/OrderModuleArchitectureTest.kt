package com.makarytskyi.rentcar.order.arch

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.onionArchitecture
import kotlin.test.Test

internal class OrderModuleArchitectureTest {
    @Test
    fun `module should have onion architecture`() {
        val rule = onionArchitecture()
            .withOptionalLayers(true)
            .domainModels("..domain..")
            .applicationServices("..application..")
            .adapter("mongo", "..infrastructure.mongo..")
            .adapter("nats", "..infrastructure.nats..")
            .adapter("kafka", "..infrastructure.kafka..")

        rule.check(importedClasses)
    }

    companion object {
        private val importedClasses: JavaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages("com.makarytskyi.rentcar.order")
    }
}
