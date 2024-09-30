package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoOrder
import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.model.projection.AggregatedMongoOrder
import com.makarytskyi.rentcar.repository.OrderRepository
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.aggregation.ArrayOperators
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
internal class OrderRepositoryImpl(private val template: MongoTemplate) : OrderRepository {

    override fun findById(id: String): AggregatedMongoOrder? {
        val match = Aggregation.match(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))

        val lookupCars = Aggregation.lookup(MongoCar.COLLECTION_NAME, MongoOrder::carId.name, "_id", "car")
        val lookupUsers = Aggregation.lookup(MongoUser.COLLECTION_NAME, MongoOrder::userId.name, "_id", "user")

        val addFields = Aggregation.addFields()
            .addField("car").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$car").elementAt(0))
            .addField("user").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$user").elementAt(0)).build()

        val project = Aggregation.project().andExclude(MongoOrder::carId.name, MongoOrder::userId.name)

        val aggregation = newAggregation(match, lookupCars, lookupUsers, addFields, project)
        val results: AggregationResults<AggregatedMongoOrder> =
            template.aggregate(aggregation, MongoOrder.COLLECTION_NAME, AggregatedMongoOrder::class.java)
        return results.mappedResults.firstOrNull()
    }

    override fun findAll(page: Int, size: Int): List<AggregatedMongoOrder> {
        val skipAmount = page * size
        val skip = Aggregation.skip(skipAmount.toLong())
        val limit = Aggregation.limit(size.toLong())
        val lookupCars = Aggregation.lookup(MongoCar.COLLECTION_NAME, MongoOrder::carId.name, "_id", "car")
        val lookupUsers = Aggregation.lookup(MongoUser.COLLECTION_NAME, MongoOrder::userId.name, "_id", "user")

        val addFields = Aggregation.addFields()
            .addField("car").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$car").elementAt(0))
            .addField("user").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$user").elementAt(0)).build()

        val project = Aggregation.project().andExclude(MongoOrder::carId.name, MongoOrder::userId.name)
        val aggregation = newAggregation(skip, limit, lookupCars, lookupUsers, addFields, project)
        val results: AggregationResults<AggregatedMongoOrder> =
            template.aggregate(aggregation, MongoOrder.COLLECTION_NAME, AggregatedMongoOrder::class.java)
        return results.mappedResults
    }

    override fun create(mongoOrder: MongoOrder): MongoOrder {
        return template.insert(mongoOrder)
    }

    override fun deleteById(id: String) {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        template.remove(query, MongoOrder::class.java)
    }

    override fun findByUserId(userId: String): List<MongoOrder> {
        val query = Query(
            Criteria.where(MongoOrder::userId.name).isEqualTo(ObjectId(userId))
        )
        return template.find(query, MongoOrder::class.java)
    }

    override fun findByCarId(carId: String): List<MongoOrder> {
        val query = Query(
            Criteria.where(MongoOrder::carId.name).isEqualTo(ObjectId(carId))
        )
        return template.find(query, MongoOrder::class.java)
    }

    override fun patch(id: String, mongoOrder: MongoOrder): MongoOrder? {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(id))
        val update = Update()

        mongoOrder.from?.let { update.set(MongoOrder::from.name, it) }
        mongoOrder.to?.let { update.set(MongoOrder::to.name, it) }

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoOrder::class.java)
    }

    override fun findByCarIdAndUserId(carId: String, userId: String): List<MongoOrder> {
        val query = Query(
            Criteria
                .where(MongoOrder::carId.name).isEqualTo(ObjectId(carId))
                .and(MongoOrder::userId.name).isEqualTo(ObjectId(userId))
        )
        return template.find(query, MongoOrder::class.java)
    }
}
