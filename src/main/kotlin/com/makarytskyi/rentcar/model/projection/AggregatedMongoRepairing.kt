package com.makarytskyi.rentcar.model.projection

import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoRepairing.RepairingStatus
import java.math.BigDecimal
import java.util.Date
import org.bson.types.ObjectId

data class AggregatedMongoRepairing(
    val id: ObjectId?,
    val car: MongoCar?,
    val date: Date?,
    val price: BigDecimal?,
    val status: RepairingStatus?,
)
