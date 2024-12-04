package com.makarytskyi.rentcar.order.infrastructure.mongo.entity

import java.util.Date
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("Order")
@Document(collection = MongoOrder.COLLECTION_NAME)
data class MongoOrder(
    @Id
    val id: ObjectId? = null,
    val carId: ObjectId?,
    val userId: ObjectId?,
    val from: Date?,
    val to: Date?,
) {
    companion object {
        const val COLLECTION_NAME = "orders"
    }
}
