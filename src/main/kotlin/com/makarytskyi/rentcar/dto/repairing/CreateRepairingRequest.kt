package com.makarytskyi.rentcar.dto.repairing

import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.model.Repairing.RepairingStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.util.Date

data class CreateRepairingRequest(
    @field:NotBlank
    val carId: String,
    val date: Date?,
    @field:Min(0)
    val price: Int?,
    val status: RepairingStatus?,
) {

    fun toEntity(): Repairing = Repairing(
        carId = carId,
        date = date,
        price = price,
        status = status,
    )
}
