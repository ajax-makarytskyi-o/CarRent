package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.MongoRepairing.RepairingStatus
import com.makarytskyi.rentcar.model.projection.AggregatedMongoRepairing
import com.makarytskyi.rentcar.repository.RepairingRepository
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
internal class RepairingRepositoryImpl(private val template: MongoTemplate) : RepairingRepository {

    override fun create(mongoRepairing: MongoRepairing): MongoRepairing {
        return template.insert(mongoRepairing)
    }

    override fun findById(id: String): AggregatedMongoRepairing? {
        val match = Aggregation.match(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val lookup = Aggregation.lookup().from(MongoCar.COLLECTION_NAME).localField(MongoRepairing::carId.name)
            .foreignField("_id").`as`("car")
        val unwind = Aggregation.unwind("car")
        val project = Aggregation.project().andExclude(MongoRepairing::carId.name)

        val aggregation = newAggregation(match, lookup, unwind, project)
        val results: AggregationResults<AggregatedMongoRepairing> =
            template.aggregate(aggregation, MongoRepairing.COLLECTION_NAME, AggregatedMongoRepairing::class.java)
        return results.mappedResults.firstOrNull()
    }

    override fun findAll(): List<AggregatedMongoRepairing> {
        val lookup = Aggregation.lookup().from(MongoCar.COLLECTION_NAME).localField(MongoRepairing::carId.name)
            .foreignField("_id").`as`("car")
        val unwind = Aggregation.unwind("car")
        val project = Aggregation.project().andExclude(MongoRepairing::carId.name)
        val aggregation = newAggregation(lookup, unwind, project)
        val results: AggregationResults<AggregatedMongoRepairing> =
            template.aggregate(aggregation, MongoRepairing.COLLECTION_NAME, AggregatedMongoRepairing::class.java)
        return results.mappedResults
    }

    override fun deleteById(id: String) {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        template.remove(query, MongoRepairing::class.java)
    }

    override fun update(id: String, mongoRepairing: MongoRepairing): MongoRepairing? {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val update = Update()

        mongoRepairing.price?.let { update.set(MongoRepairing::price.name, it) }
        mongoRepairing.status?.let { update.set(MongoRepairing::status.name, it) }

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoRepairing::class.java)
    }

    override fun findByStatusAndCarId(status: RepairingStatus, carId: String): List<MongoRepairing> {
        val query = Query(
            Criteria
                .where(MongoRepairing::carId.name).isEqualTo(ObjectId(carId))
                .and(MongoRepairing::status.name).isEqualTo(status)
        )
        return template.find(query, MongoRepairing::class.java)
    }

    override fun findByStatus(status: RepairingStatus): List<MongoRepairing> {
        val query = Query(
            Criteria.where(MongoRepairing::status.name).isEqualTo(status)
        )
        return template.find(query, MongoRepairing::class.java)
    }

    override fun findByCarId(carId: String): List<MongoRepairing> {
        val query = Query(
            Criteria.where(MongoRepairing::carId.name).isEqualTo(ObjectId(carId))
        )
        return template.find(query, MongoRepairing::class.java)
    }
}
