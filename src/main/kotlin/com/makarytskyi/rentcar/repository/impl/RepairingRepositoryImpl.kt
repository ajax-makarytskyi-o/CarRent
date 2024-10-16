package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.MongoRepairing.RepairingStatus
import com.makarytskyi.rentcar.model.patch.MongoRepairingPatch
import com.makarytskyi.rentcar.model.projection.AggregatedMongoRepairing
import com.makarytskyi.rentcar.repository.RepairingRepository
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
internal class RepairingRepositoryImpl(private val template: ReactiveMongoTemplate) : RepairingRepository {

    override fun create(mongoRepairing: MongoRepairing): Mono<MongoRepairing> {
        return template.insert(mongoRepairing)
    }

    override fun findFullById(id: String): Mono<AggregatedMongoRepairing> {
        val match = Aggregation.match(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val lookup = Aggregation.lookup().from(MongoCar.COLLECTION_NAME).localField(MongoRepairing::carId.name)
            .foreignField(Fields.UNDERSCORE_ID).`as`("car")
        val unwind = Aggregation.unwind("car")
        val project = Aggregation.project().andExclude(MongoRepairing::carId.name)

        val aggregation = newAggregation(match, lookup, unwind, project)

        return template.aggregate(aggregation, MongoRepairing.COLLECTION_NAME, AggregatedMongoRepairing::class.java)
            .singleOrEmpty()
    }

    override fun findFullAll(page: Int, size: Int): Flux<AggregatedMongoRepairing> {
        val skipAmount = page * size
        val skip = Aggregation.skip(skipAmount.toLong())
        val limit = Aggregation.limit(size.toLong())
        val lookup = Aggregation.lookup().from(MongoCar.COLLECTION_NAME).localField(MongoRepairing::carId.name)
            .foreignField("_id").`as`("car")
        val unwind = Aggregation.unwind("car")
        val project = Aggregation.project().andExclude(MongoRepairing::carId.name)
        val aggregation = newAggregation(skip, limit, lookup, unwind, project)
        return template.aggregate(aggregation, MongoRepairing.COLLECTION_NAME, AggregatedMongoRepairing::class.java)
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return template.remove(query, MongoRepairing::class.java).thenReturn(Unit)
    }

    override fun patch(id: String, repairingPatch: MongoRepairingPatch): Mono<MongoRepairing> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val update = Update()

        repairingPatch.price?.let { update.set(MongoRepairing::price.name, it) }
        repairingPatch.status?.let { update.set(MongoRepairing::status.name, it) }

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoRepairing::class.java)
    }

    override fun findByStatusAndCarId(status: RepairingStatus, carId: String): Flux<MongoRepairing> {
        val query = Query(
            Criteria
                .where(MongoRepairing::carId.name).isEqualTo(ObjectId(carId))
                .and(MongoRepairing::status.name).isEqualTo(status)
        )
        return template.find(query, MongoRepairing::class.java)
    }

    override fun findByStatus(status: RepairingStatus): Flux<MongoRepairing> {
        val query = Query(
            Criteria.where(MongoRepairing::status.name).isEqualTo(status)
        )
        return template.find(query, MongoRepairing::class.java)
    }

    override fun findByCarId(carId: String): Flux<MongoRepairing> {
        val query = Query(
            Criteria.where(MongoRepairing::carId.name).isEqualTo(ObjectId(carId))
        )
        return template.find(query, MongoRepairing::class.java)
    }
}
