package com.makarytskyi.rentcar.repairing.infrastructure.rest.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.util.Date

data class CreateRepairingRequest(
    @field:NotBlank
    val carId: String,
    val date: Date?,
    @field:Min(0)
    val price: BigDecimal?,
    val status: RepairingStatus?,
) {
    enum class RepairingStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }
}
