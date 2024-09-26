package com.makarytskyi.rentcar.model.aggregated

import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoUser
import java.util.Date
import org.bson.types.ObjectId

data class AggregatedMongoOrder(
    val id: ObjectId?,
    val car: MongoCar?,
    val user: MongoUser?,
    val from: Date?,
    val to: Date?,
)
