package com.makarytskyi.rentcar.mongock.migration

import com.makarytskyi.rentcar.car.infrastructure.mongo.entity.MongoCar
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.index.PartialIndexFilter
import org.springframework.data.mongodb.core.query.Criteria

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
                .on(MongoCar::brand.name, Sort.Direction.ASC)
                .on(MongoCar::model.name, Sort.Direction.ASC)
        )

        indexOps.ensureIndex(
            Index()
                .on(MongoCar::plate.name, Sort.Direction.ASC)
                .unique()
                .partial(PartialIndexFilter.of(Criteria.where(MongoCar::plate.name).exists(true)))
        )
        log.info("Indexes for {} collection were created", MongoCar.COLLECTION_NAME)
    }

    @RollbackExecution
    fun rollbackCarCollection(mongoTemplate: MongoTemplate) {
        if (mongoTemplate.collectionExists(MongoCar.COLLECTION_NAME)) {
            mongoTemplate.dropCollection(MongoCar.COLLECTION_NAME)
            log.info("Collection {} was dropped", MongoCar.COLLECTION_NAME)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CarMigration::class.java)
    }
}
