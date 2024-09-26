package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.MongoOrder
import com.makarytskyi.rentcar.model.aggregated.AggregatedMongoOrder
import com.makarytskyi.rentcar.repository.OrderRepository
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation.addField
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.aggregation.ArrayOperators
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
internal class OrderRepositoryImpl(private val template: MongoTemplate) : OrderRepository {

    override fun findById(id: String): AggregatedMongoOrder? {
        val match = Aggregation.match(Criteria.where("_id").isEqualTo(id))

        val lookupCars = Aggregation.lookup("cars", "carId", "_id", "car")
        val lookupUsers = Aggregation.lookup("users", "userId", "_id", "user")

        val addFields = Aggregation.addFields()
            .addField("car").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$car").elementAt(0))
            .addField("user").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$user").elementAt(0)).build()

        val project = Aggregation.project().andExclude("carId", "userId")

        val aggregation = newAggregation(match, lookupCars, lookupUsers, addFields, project)
        val results: AggregationResults<AggregatedMongoOrder> =
            template.aggregate(aggregation, MongoOrder.COLLECTION_NAME, AggregatedMongoOrder::class.java)
        return results.mappedResults.firstOrNull()
    }

    override fun findAll(): List<AggregatedMongoOrder> {
        val lookupCars = Aggregation.lookup("cars", "carId", "_id", "car")
        val lookupUsers = Aggregation.lookup("users", "userId", "_id", "user")

        val addFields = Aggregation.addFields()
            .addField("car").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$car").elementAt(0))
            .addField("user").withValue(ArrayOperators.ArrayElemAt.arrayOf("\$user").elementAt(0)).build()

        val project = Aggregation.project().andExclude("carId", "userId")
        val aggregation = newAggregation(lookupCars, lookupUsers, addFields, project)
        val results: AggregationResults<AggregatedMongoOrder> =
            template.aggregate(aggregation, MongoOrder.COLLECTION_NAME, AggregatedMongoOrder::class.java)
        return results.mappedResults
    }

    override fun create(mongoOrder: MongoOrder): MongoOrder = template.insert(mongoOrder)

    override fun deleteById(id: String) {
        val query = Query(Criteria.where("_id").isEqualTo(id))
        template.remove(query, MongoOrder::class.java)
    }

    override fun findByUserId(userId: String): List<MongoOrder> {
        val query = Query(
            Criteria.where("userId").isEqualTo(ObjectId(userId))
        )
        return template.find(query, MongoOrder::class.java)
    }

    override fun findByCarId(carId: String): List<MongoOrder> {
        val query = Query(
            Criteria.where("carId").isEqualTo(ObjectId(carId))
        )
        return template.find(query, MongoOrder::class.java)
    }

    override fun update(id: String, mongoOrder: MongoOrder): MongoOrder? {
        val query = Query(Criteria.where("_id").isEqualTo(id))
        val update = Update()

        mongoOrder.from?.let { update.set("from", it) }
        mongoOrder.to?.let { update.set("to", it) }

        val options = FindAndModifyOptions()
        options.returnNew(true)

        return template.findAndModify(query, update, options, MongoOrder::class.java)
    }

    override fun findByCarIdAndUserId(carId: String, userId: String): List<MongoOrder> {
        val query = Query(
            Criteria.where("carId").isEqualTo(ObjectId(carId)).and("userId").isEqualTo(ObjectId(userId))
        )
        return template.find(query, MongoOrder::class.java)
    }
}
