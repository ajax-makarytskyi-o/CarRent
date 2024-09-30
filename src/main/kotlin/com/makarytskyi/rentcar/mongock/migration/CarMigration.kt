package com.makarytskyi.rentcar.mongock.migration

import com.makarytskyi.rentcar.model.MongoCar
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index

@ChangeUnit(id = "carCollectionAndIndexes", order = "1", author = "makarytskyi.o@ajax.systems")
class CarMigration {

    @Execution
    fun createCarCollection(mongoTemplate: MongoTemplate) {
        if (!mongoTemplate.collectionExists(MongoCar.COLLECTION_NAME)) {
            mongoTemplate.createCollection(MongoCar.COLLECTION_NAME)
            log.info("Collection {} was created", MongoCar.COLLECTION_NAME)
        }

        val indexOps = mongoTemplate.indexOps(MongoCar.COLLECTION_NAME)
        indexOps.ensureIndex(
            Index()
                .on("brand", Sort.Direction.ASC)
                .on("model", Sort.Direction.ASC)
                .named("cars_brand_model_index")
        )

        indexOps.ensureIndex(
            Index()
                .on("plate", Sort.Direction.ASC).unique()
                .named("cars_plate_index")
        )
        log.info("Indexes for {} collection were created", MongoCar.COLLECTION_NAME)
    }

    @RollbackExecution
    fun rollbackCarCollection(mongoTemplate: MongoTemplate) {
        val indexOps = mongoTemplate.indexOps(MongoCar.COLLECTION_NAME)
        val indexes = indexOps.indexInfo

        if (indexes.any { it.name == "cars_brand_model_index" }) {
            indexOps.dropIndex("cars_brand_model_index")
            log.info("Index 'cars_brand_model_index' for collection {} was rolled back", MongoCar.COLLECTION_NAME)
        }

        if (indexes.any { it.name == "cars_plate_index" }) {
            indexOps.dropIndex("cars_plate_index")
            log.info("Index 'cars_plate_index' for collection {} was rolled back", MongoCar.COLLECTION_NAME)
        }

        if (mongoTemplate.collectionExists(MongoCar.COLLECTION_NAME)) {
            mongoTemplate.dropCollection(MongoCar.COLLECTION_NAME)
            log.info("Collection {} was dropped", MongoCar.COLLECTION_NAME)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CarMigration::class.java)
    }
}
