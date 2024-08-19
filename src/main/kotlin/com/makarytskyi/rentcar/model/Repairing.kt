package com.makarytskyi.rentcar.model

import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import java.util.Date

data class Repairing(
    val id: String? = null,
    val carId: String?,
    val date: Date?,
    val price: Int?,
    val status: RepairingStatus?,
) {

    enum class RepairingStatus {
        PENDING, IN_PROGRESS, COMPLETED;
    }

    fun toResponse(): RepairingResponse = RepairingResponse(
        id ?: "none",
        carId ?: "none",
        date,
        price,
        status,
    )
}
