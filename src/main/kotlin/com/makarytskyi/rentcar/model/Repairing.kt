package com.makarytskyi.rentcar.model

import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class Repairing (
    val id: String?,
    val carId: String?,
    val date: Date?,
    val price: Int?,
    val status: RepairingStatus?
) {

    enum class RepairingStatus {
        PENDING, IN_PROGRESS, COMPLETED;
    }

    fun toResponse(): RepairingResponse = RepairingResponse(
        id,
        carId ?: throw IllegalArgumentException("Car of repairing is null"),
        date ?: throw IllegalArgumentException("Date of repairing is null"),
        price ?: throw IllegalArgumentException("Price of repairing is null"),
        status
    )
}
