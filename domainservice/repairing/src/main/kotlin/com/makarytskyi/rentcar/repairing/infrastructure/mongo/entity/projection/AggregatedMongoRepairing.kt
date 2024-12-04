package com.makarytskyi.rentcar.repairing.infrastructure.mongo.entity.projection

import com.makarytskyi.rentcar.car.infrastructure.mongo.entity.MongoCar
import com.makarytskyi.rentcar.repairing.infrastructure.mongo.entity.MongoRepairing
import java.math.BigDecimal
import java.util.Date
import org.bson.types.ObjectId

data class AggregatedMongoRepairing(
    val id: ObjectId?,
    val car: MongoCar?,
    val date: Date?,
    val price: BigDecimal?,
    val status: MongoRepairing.RepairingStatus?,
)
