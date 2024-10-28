package com.makarytskyi.rentcar.mongock.migration
//
//import com.makarytskyi.rentcar.model.MongoRepairing
//import io.mongock.api.annotations.ChangeUnit
//import io.mongock.api.annotations.Execution
//import io.mongock.api.annotations.RollbackExecution
//import org.slf4j.LoggerFactory
//import org.springframework.data.domain.Sort
//import org.springframework.data.mongodb.core.MongoTemplate
//import org.springframework.data.mongodb.core.index.Index
//
//@ChangeUnit(id = "repairingCollectionAndIndex", order = "3", author = "makarytskyi.o@ajax.systems")
//class RepairingMigration {
//
//    @Execution
//    fun createRepairingCollection(mongoTemplate: MongoTemplate) {
//        if (!mongoTemplate.collectionExists(MongoRepairing.COLLECTION_NAME)) {
//            mongoTemplate.createCollection(MongoRepairing.COLLECTION_NAME)
//            log.info("Collection {} was created", MongoRepairing.COLLECTION_NAME)
//        }
//
//        val indexOps = mongoTemplate.indexOps(MongoRepairing.COLLECTION_NAME)
//        indexOps.ensureIndex(
//            Index()
//                .on(MongoRepairing::status.name, Sort.Direction.ASC)
//                .on(MongoRepairing::carId.name, Sort.Direction.ASC)
//        )
//
//        indexOps.ensureIndex(
//            Index()
//                .on(MongoRepairing::carId.name, Sort.Direction.ASC)
//        )
//
//        log.info("Indexes for {} collection were created", MongoRepairing.COLLECTION_NAME)
//    }
//
//    @RollbackExecution
//    fun rollbackRepairingCollection(mongoTemplate: MongoTemplate) {
//        if (mongoTemplate.collectionExists(MongoRepairing.COLLECTION_NAME)) {
//            mongoTemplate.dropCollection(MongoRepairing.COLLECTION_NAME)
//            log.info("Collection {} was dropped", MongoRepairing.COLLECTION_NAME)
//        }
//    }
//
//    companion object {
//        private val log = LoggerFactory.getLogger(RepairingMigration::class.java)
//    }
//}
