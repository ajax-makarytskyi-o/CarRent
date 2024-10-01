package com.makarytskyi.rentcar.dto.repairing

import com.makarytskyi.rentcar.model.MongoRepairing
import java.math.BigDecimal
import java.util.Date

data class RepairingResponse(
    val id: String,
    val carId: String,
    val date: Date?,
    val price: BigDecimal?,
    val status: MongoRepairing.RepairingStatus?,
) {

    companion object {
        fun from(mongoRepairing: MongoRepairing): RepairingResponse = RepairingResponse(
            mongoRepairing.id?.toString().orEmpty(),
            mongoRepairing.carId?.toString().orEmpty(),
            mongoRepairing.date,
            mongoRepairing.price,
            mongoRepairing.status,
        )
    }
}
