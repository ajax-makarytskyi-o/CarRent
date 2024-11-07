package com.makarytskyi.rentcar.dto.repairing

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.projection.AggregatedMongoRepairing
import java.math.BigDecimal
import java.util.Date

data class AggregatedRepairingResponse(
    val id: String,
    val car: CarResponse,
    val date: Date?,
    val price: BigDecimal?,
    val status: MongoRepairing.RepairingStatus?,
) {

    companion object {
        fun from(mongoRepairing: AggregatedMongoRepairing): AggregatedRepairingResponse = AggregatedRepairingResponse(
            requireNotNull(mongoRepairing.id?.toString()) { "Repairing id is null" },
            CarResponse.from(mongoRepairing.car ?: MongoCar()),
            mongoRepairing.date,
            mongoRepairing.price,
            mongoRepairing.status,
        )
    }
}
