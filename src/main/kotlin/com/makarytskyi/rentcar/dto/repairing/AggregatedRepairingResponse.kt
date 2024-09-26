package com.makarytskyi.rentcar.dto.repairing

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.aggregated.AggregatedMongoRepairing
import java.util.Date

data class AggregatedRepairingResponse(
    val id: String,
    val car: CarResponse?,
    val date: Date?,
    val price: Int?,
    val status: MongoRepairing.RepairingStatus?,
) {

    companion object {
        fun from(mongoRepairing: AggregatedMongoRepairing): AggregatedRepairingResponse = AggregatedRepairingResponse(
            mongoRepairing.id?.toString().orEmpty(),
            mongoRepairing.car?.let { CarResponse.from(it) },
            mongoRepairing.date,
            mongoRepairing.price,
            mongoRepairing.status,
        )
    }
}
