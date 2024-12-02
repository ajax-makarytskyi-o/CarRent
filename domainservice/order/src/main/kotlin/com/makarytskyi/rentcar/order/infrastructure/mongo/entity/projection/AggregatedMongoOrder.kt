package com.makarytskyi.rentcar.order.infrastructure.mongo.entity.projection

import com.makarytskyi.rentcar.car.infrastructure.mongo.entity.MongoCar
import com.makarytskyi.rentcar.user.infrastructure.mongo.entity.MongoUser
import java.util.Date
import org.bson.types.ObjectId

data class AggregatedMongoOrder(
    val id: ObjectId?,
    val car: MongoCar?,
    val user: MongoUser?,
    val from: Date?,
    val to: Date?,
)
