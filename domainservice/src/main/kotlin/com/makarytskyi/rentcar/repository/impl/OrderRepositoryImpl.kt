package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoOrder
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.model.patch.MongoOrderPatch
import com.makarytskyi.rentcar.model.projection.AggregatedMongoOrder
import com.makarytskyi.rentcar.repository.OrderRepository
import java.util.Date
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.ArrayOperators
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
internal class OrderRepositoryImpl(private val template: ReactiveMongoTemplate) : OrderRepository {

    override fun findFullById(id: String): Mono<AggregatedMongoOrder> {
        val match = Aggregation.match(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))

        val lookupCars = Aggregation.lookup().from(MongoCar.COLLECTION_NAME).localField(MongoRepairing::carId.name)
            .foreignField(Fields.UNDERSCORE_ID).`as`("car")
        val lookupUsers = Aggregation.lookup().from(MongoUser.COLLECTION_NAME).localField(MongoOrder::userId.name)
            .foreignField(Fields.UNDERSCORE_ID).`as`("user")

        val addFields = Aggregation.addFields()
            .addField("car").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$car").elementAt(0))
            .addField("user").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$user").elementAt(0)).build()

        val project = Aggregation.project().andExclude(MongoOrder::carId.name, MongoOrder::userId.name)

        val aggregation = newAggregation(match, lookupCars, lookupUsers, addFields, project)

        return template.aggregate(aggregation, MongoOrder.COLLECTION_NAME, AggregatedMongoOrder::class.java)
            .singleOrEmpty()
    }

    override fun findFullAll(page: Int, size: Int): Flux<AggregatedMongoOrder> {
        val skipAmount = page * size
        val skip = Aggregation.skip(skipAmount.toLong())
        val limit = Aggregation.limit(size.toLong())
        val lookupCars = Aggregation.lookup().from(MongoCar.COLLECTION_NAME).localField(MongoRepairing::carId.name)
            .foreignField(Fields.UNDERSCORE_ID).`as`("car")
        val lookupUsers = Aggregation.lookup().from(MongoUser.COLLECTION_NAME).localField(MongoOrder::userId.name)
            .foreignField(Fields.UNDERSCORE_ID).`as`("user")

        val addFields = Aggregation.addFields()
            .addField("car").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$car").elementAt(0))
            .addField("user").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$user").elementAt(0)).build()

        val project = Aggregation.project().andExclude(MongoOrder::carId.name, MongoOrder::userId.name)
        val aggregation = newAggregation(skip, limit, lookupCars, lookupUsers, addFields, project)

        return template.aggregate(aggregation, MongoOrder.COLLECTION_NAME, AggregatedMongoOrder::class.java)
    }

    override fun create(mongoOrder: MongoOrder): Mono<MongoOrder> {
        return template.insert(mongoOrder)
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return template.remove(query, MongoOrder::class.java).thenReturn(Unit)
    }

    override fun findByUserId(userId: String): Flux<MongoOrder> {
        val query = Query(
            Criteria.where(MongoOrder::userId.name).isEqualTo(ObjectId(userId))
        )
        return template.find(query, MongoOrder::class.java)
    }

    override fun findByCarId(carId: String): Flux<MongoOrder> {
        val query = Query(
            Criteria.where(MongoOrder::carId.name).isEqualTo(ObjectId(carId))
        )
        return template.find(query, MongoOrder::class.java)
    }

    override fun patch(id: String, orderPatch: MongoOrderPatch): Mono<MongoOrder> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val update = Update()

        orderPatch.from?.let { update.set(MongoOrder::from.name, it) }
        orderPatch.to?.let { update.set(MongoOrder::to.name, it) }

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoOrder::class.java)
    }

    override fun findByCarIdAndUserId(carId: String, userId: String): Flux<MongoOrder> {
        val query = Query(
            Criteria
                .where(MongoOrder::carId.name).isEqualTo(ObjectId(carId))
                .and(MongoOrder::userId.name).isEqualTo(ObjectId(userId))
        )
        return template.find(query, MongoOrder::class.java)
    }

    override fun findOrderByCarIdAndDate(carId: String, date: Date): Mono<MongoOrder> {
        val query = Query(
            Criteria
                .where(MongoOrder::carId.name).isEqualTo(ObjectId(carId))
                .and(MongoOrder::from.name).lte(date)
                .and(MongoOrder::to.name).gte(date)
        )
        return template.findOne(query, MongoOrder::class.java)
    }
}
