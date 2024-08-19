package com.makarytskyi.rentcar.dto.repairing

import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.model.Repairing.RepairingStatus
import java.util.*

data class CreateRepairingRequest(
    val carId: String?,
    val date: Date?,
    val price: Int?,
    val status: RepairingStatus?
) {

    fun toEntity(): Repairing = Repairing(
        null,
        carId ?: throw IllegalArgumentException("Car of repairing is null"),
        date ?: throw IllegalArgumentException("Date of repairing is null"),
        price ?: throw IllegalArgumentException("Price of repairing is null"),
        status
    )
}
