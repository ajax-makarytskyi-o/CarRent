package com.makarytskyi.rentcar.mongock.migration

import com.makarytskyi.rentcar.model.MongoOrder
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index

@ChangeUnit(id = "orderCollectionAndIndex", order = "4", author = "makarytskyi.o@ajax.systems")
class OrderMigration {

    @Execution
    fun createOrderCollection(mongoTemplate: MongoTemplate) {
        if (!mongoTemplate.collectionExists(MongoOrder.COLLECTION_NAME)) {
            mongoTemplate.createCollection(MongoOrder.COLLECTION_NAME)
            log.info("Collection {} was created", MongoOrder.COLLECTION_NAME)
        }

        val indexOps = mongoTemplate.indexOps(MongoOrder.COLLECTION_NAME)
        indexOps.ensureIndex(
            Index()
                .on(MongoOrder::carId.name, Sort.Direction.ASC)
        )
        indexOps.ensureIndex(
            Index()
                .on(MongoOrder::userId.name, Sort.Direction.ASC)
        )
        log.info("Indexes for {} collection were created", MongoOrder.COLLECTION_NAME)
    }

    @RollbackExecution
    fun rollbackOrderCollection(mongoTemplate: MongoTemplate) {
        if (mongoTemplate.collectionExists(MongoOrder.COLLECTION_NAME)) {
            mongoTemplate.dropCollection(MongoOrder.COLLECTION_NAME)
            log.info("Collection {} was dropped", MongoOrder.COLLECTION_NAME)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderMigration::class.java)
    }
}
