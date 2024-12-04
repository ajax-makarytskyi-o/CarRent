package com.makarytskyi.rentcar.repairing.infrastructure.mongo

import com.makarytskyi.rentcar.car.infrastructure.mongo.entity.MongoCar
import com.makarytskyi.rentcar.repairing.application.port.output.RepairingRepositoryOutputPort
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.domain.create.CreateRepairing
import com.makarytskyi.rentcar.repairing.domain.projection.AggregatedDomainRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.mongo.entity.MongoRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.mongo.entity.projection.AggregatedMongoRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.mongo.mapper.toDomain
import com.makarytskyi.rentcar.repairing.infrastructure.mongo.mapper.toMongo
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
internal class MongoRepairingRepository(private val template: ReactiveMongoTemplate) : RepairingRepositoryOutputPort {

    override fun create(repairing: CreateRepairing): Mono<DomainRepairing> {
        return template.insert(repairing.toMongo()).map { it.toDomain() }
    }

    override fun findFullById(id: String): Mono<AggregatedDomainRepairing> {
        val match = Aggregation.match(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val lookup = Aggregation.lookup().from(MongoCar.COLLECTION_NAME).localField(MongoRepairing::carId.name)
            .foreignField(Fields.UNDERSCORE_ID).`as`("car")
        val unwind = Aggregation.unwind("car")
        val project = Aggregation.project().andExclude(MongoRepairing::carId.name)

        val aggregation = newAggregation(match, lookup, unwind, project)

        return template.aggregate(aggregation, MongoRepairing.COLLECTION_NAME, AggregatedMongoRepairing::class.java)
            .singleOrEmpty().map { it.toDomain() }
    }

    override fun findById(id: String): Mono<DomainRepairing> {
        return template.findById<MongoRepairing>(id).map { it.toDomain() }
    }

    override fun findFullAll(page: Int, size: Int): Flux<AggregatedDomainRepairing> {
        val skipAmount = page * size
        val skip = Aggregation.skip(skipAmount.toLong())
        val limit = Aggregation.limit(size.toLong())
        val lookup = Aggregation.lookup().from(MongoCar.COLLECTION_NAME).localField(MongoRepairing::carId.name)
            .foreignField("_id").`as`("car")
        val unwind = Aggregation.unwind("car")
        val project = Aggregation.project().andExclude(MongoRepairing::carId.name)
        val aggregation = newAggregation(skip, limit, lookup, unwind, project)
        return template.aggregate(aggregation, MongoRepairing.COLLECTION_NAME, AggregatedMongoRepairing::class.java)
            .map { it.toDomain() }
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return template.remove(query, MongoRepairing::class.java).thenReturn(Unit)
    }

    override fun patch(id: String, patch: DomainRepairing): Mono<DomainRepairing> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val update = Update()
            .set(MongoRepairing::price.name, patch.price)
            .set(MongoRepairing::status.name, patch.status)

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoRepairing::class.java).map { it.toDomain() }
    }

    override fun findByStatusAndCarId(status: DomainRepairing.RepairingStatus, carId: String): Flux<DomainRepairing> {
        val query = Query(
            Criteria
                .where(MongoRepairing::carId.name).isEqualTo(ObjectId(carId))
                .and(MongoRepairing::status.name).isEqualTo(status)
        )
        return template.find(query, MongoRepairing::class.java).map { it.toDomain() }
    }

    override fun findByStatus(status: DomainRepairing.RepairingStatus): Flux<DomainRepairing> {
        val query = Query(
            Criteria.where(MongoRepairing::status.name).isEqualTo(status)
        )
        return template.find(query, MongoRepairing::class.java).map { it.toDomain() }
    }

    override fun findByCarId(carId: String): Flux<DomainRepairing> {
        val query = Query(
            Criteria.where(MongoRepairing::carId.name).isEqualTo(ObjectId(carId))
        )
        return template.find(query, MongoRepairing::class.java).map { it.toDomain() }
    }
}
