package com.makarytskyi.rentcar.mongock.migration

import com.makarytskyi.rentcar.model.MongoOrder
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index

@ChangeUnit(id = "orderCollectionAndIndex", order = "004", author = "Makarytskyi Oleh")
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
                .named("carId_index")
        )
        indexOps.ensureIndex(
            Index()
                .on("userId", Sort.Direction.ASC)
                .named("userId_index")
        )
        log.info("Indexes for {} collection were created", MongoOrder.COLLECTION_NAME)
    }

    @RollbackExecution
    fun rollbackOrderCollection(mongoTemplate: MongoTemplate) {
        val indexOps = mongoTemplate.indexOps(MongoOrder.COLLECTION_NAME)
        indexOps.dropIndex("carId_index")
        indexOps.dropIndex("userId_index")
        log.info("Indexes for {} collection were rolled back", MongoOrder.COLLECTION_NAME)

        if (mongoTemplate.collectionExists(MongoOrder.COLLECTION_NAME)) {
            mongoTemplate.dropCollection(MongoOrder.COLLECTION_NAME)
            log.info("Collection {} was dropped", MongoOrder.COLLECTION_NAME)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderMigration::class.java)
    }
}
