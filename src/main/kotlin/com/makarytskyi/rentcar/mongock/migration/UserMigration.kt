package com.makarytskyi.rentcar.mongock.migration

import com.makarytskyi.rentcar.model.MongoUser
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index

@ChangeUnit(id = "userCollectionAndIndex", order = "2", author = "makarytskyi.o@ajax.systems")
class UserMigration {

    @Execution
    fun createUserCollection(mongoTemplate: MongoTemplate) {
        if (!mongoTemplate.collectionExists(MongoUser.COLLECTION_NAME)) {
            mongoTemplate.createCollection(MongoUser.COLLECTION_NAME)
            log.info("Collection {} was created", MongoUser.COLLECTION_NAME)
        }

        val indexOps = mongoTemplate.indexOps(MongoUser.COLLECTION_NAME)
        indexOps.ensureIndex(
            Index()
                .on("name", Sort.Direction.ASC)
        )

        indexOps.ensureIndex(
            Index()
                .on("email", Sort.Direction.ASC).unique()
        )

        indexOps.ensureIndex(
            Index()
                .on("phoneNumber", Sort.Direction.ASC).unique()
        )

        log.info("Indexes for {} collection were created", MongoUser.COLLECTION_NAME)
    }

    @RollbackExecution
    fun rollbackUserCollection(mongoTemplate: MongoTemplate) {
        if (mongoTemplate.collectionExists(MongoUser.COLLECTION_NAME)) {
            mongoTemplate.dropCollection(MongoUser.COLLECTION_NAME)
            log.info("Collection {} was dropped", MongoUser.COLLECTION_NAME)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserMigration::class.java)
    }
}
