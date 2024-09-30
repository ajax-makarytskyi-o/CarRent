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
                .on("carId", Sort.Direction.ASC)
                .named("orders_carId_index")
        )
        indexOps.ensureIndex(
            Index()
                .on("userId", Sort.Direction.ASC)
                .named("orders_userId_index")
        )
        log.info("Indexes for {} collection were created", MongoOrder.COLLECTION_NAME)
    }

    @RollbackExecution
    fun rollbackOrderCollection(mongoTemplate: MongoTemplate) {
        val indexOps = mongoTemplate.indexOps(MongoOrder.COLLECTION_NAME)
        val indexes = indexOps.indexInfo
        if (indexes.any { it.name == "orders_carId_index" }) {
            indexOps.dropIndex("orders_carId_index")
            log.info("Index 'orders_carId_index' for collection {} was rolled back", MongoOrder.COLLECTION_NAME)
        }

        if (indexes.any { it.name == "orders_userId_index" }) {
            indexOps.dropIndex("orders_userId_index")
            log.info("Index 'orders_userId_index' for collection {} was rolled back", MongoOrder.COLLECTION_NAME)
        }

        if (mongoTemplate.collectionExists(MongoOrder.COLLECTION_NAME)) {
            mongoTemplate.dropCollection(MongoOrder.COLLECTION_NAME)
            log.info("Collection {} was dropped", MongoOrder.COLLECTION_NAME)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderMigration::class.java)
    }
}
