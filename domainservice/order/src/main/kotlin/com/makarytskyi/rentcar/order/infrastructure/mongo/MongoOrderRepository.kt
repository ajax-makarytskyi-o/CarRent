package com.makarytskyi.rentcar.order.infrastructure.mongo

import com.makarytskyi.rentcar.order.application.port.output.OrderMongoOutputPort
import com.makarytskyi.rentcar.car.infrastructure.mongo.entity.MongoCar
import com.makarytskyi.rentcar.car.infrastructure.mongo.mapper.toDomain
import com.makarytskyi.rentcar.order.domain.DomainOrder
import com.makarytskyi.rentcar.order.domain.patch.DomainOrderPatch
import com.makarytskyi.rentcar.order.domain.projection.AggregatedDomainOrder
import com.makarytskyi.rentcar.order.infrastructure.mongo.entity.MongoOrder
import com.makarytskyi.rentcar.order.infrastructure.mongo.entity.projection.AggregatedMongoOrder
import com.makarytskyi.rentcar.order.infrastructure.mongo.mapper.toDomain
import com.makarytskyi.rentcar.order.infrastructure.mongo.mapper.toMongo
import com.makarytskyi.rentcar.user.infrastructure.mongo.entity.MongoUser
import java.util.Date
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.ArrayOperators
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
internal class MongoOrderRepository(private val template: ReactiveMongoTemplate) : OrderMongoOutputPort {

    override fun findFullById(id: String): Mono<AggregatedDomainOrder> {
        val match = Aggregation.match(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))

        val lookupCars = Aggregation.lookup().from(MongoCar.COLLECTION_NAME).localField(MongoOrder::carId.name)
            .foreignField(Fields.UNDERSCORE_ID).`as`("car")
        val lookupUsers = Aggregation.lookup().from(MongoUser.COLLECTION_NAME).localField(MongoOrder::userId.name)
            .foreignField(Fields.UNDERSCORE_ID).`as`("user")

        val addFields = Aggregation.addFields()
            .addField("car").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$car").elementAt(0))
            .addField("user").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$user").elementAt(0)).build()

        val project = Aggregation.project().andExclude(MongoOrder::carId.name, MongoOrder::userId.name)

        val aggregation = newAggregation(match, lookupCars, lookupUsers, addFields, project)

        return template.aggregate(aggregation, MongoOrder.COLLECTION_NAME, AggregatedMongoOrder::class.java)
            .singleOrEmpty().map { it.toDomain() }
    }

    override fun findById(id: String): Mono<DomainOrder> {
        return template.findById<MongoOrder>(id).map { it.toDomain() }
    }

    override fun findFullAll(page: Int, size: Int): Flux<AggregatedDomainOrder> {
        val skipAmount = page * size
        val skip = Aggregation.skip(skipAmount.toLong())
        val limit = Aggregation.limit(size.toLong())
        val lookupCars = Aggregation.lookup().from(MongoCar.COLLECTION_NAME).localField(MongoOrder::carId.name)
            .foreignField(Fields.UNDERSCORE_ID).`as`("car")
        val lookupUsers = Aggregation.lookup().from(MongoUser.COLLECTION_NAME).localField(MongoOrder::userId.name)
            .foreignField(Fields.UNDERSCORE_ID).`as`("user")

        val addFields = Aggregation.addFields()
            .addField("car").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$car").elementAt(0))
            .addField("user").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$user").elementAt(0)).build()

        val project = Aggregation.project().andExclude(MongoOrder::carId.name, MongoOrder::userId.name)
        val aggregation = newAggregation(skip, limit, lookupCars, lookupUsers, addFields, project)

        return template.aggregate(aggregation, MongoOrder.COLLECTION_NAME, AggregatedMongoOrder::class.java)
            .map { it.toDomain() }
    }

    override fun create(mongoOrder: DomainOrder): Mono<DomainOrder> {
        return template.insert(mongoOrder.toMongo()).map { it.toDomain() }
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return template.remove(query, MongoOrder::class.java).thenReturn(Unit)
    }

    override fun findByUserId(userId: String): Flux<DomainOrder> {
        val query = Query(
            Criteria.where(MongoOrder::userId.name).isEqualTo(ObjectId(userId))
        )
        return template.find(query, MongoOrder::class.java).map { it.toDomain() }
    }

    override fun findByCarId(carId: String): Flux<DomainOrder> {
        val query = Query(
            Criteria.where(MongoOrder::carId.name).isEqualTo(ObjectId(carId))
        )
        return template.find(query, MongoOrder::class.java).map { it.toDomain() }
    }

    override fun patch(id: String, patch: DomainOrder): Mono<DomainOrder> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val update = Update()
            .set(MongoOrder::from.name, patch.from)
            .set(MongoOrder::to.name, patch.to)

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoOrder::class.java).map { it.toDomain() }
    }

    override fun findByCarIdAndUserId(carId: String, userId: String): Flux<DomainOrder> {
        val query = Query(
            Criteria
                .where(MongoOrder::carId.name).isEqualTo(ObjectId(carId))
                .and(MongoOrder::userId.name).isEqualTo(ObjectId(userId))
        )
        return template.find(query, MongoOrder::class.java).map { it.toDomain() }
    }

    override fun findOrderByDateAndCarId(date: Date, carId: String): Mono<DomainOrder> {
        val query = Query(
            Criteria
                .where(MongoOrder::carId.name).isEqualTo(ObjectId(carId))
                .and(MongoOrder::from.name).lte(date)
                .and(MongoOrder::to.name).gte(date)
        )
        return template.findOne(query, MongoOrder::class.java).map { it.toDomain() }
    }
}
