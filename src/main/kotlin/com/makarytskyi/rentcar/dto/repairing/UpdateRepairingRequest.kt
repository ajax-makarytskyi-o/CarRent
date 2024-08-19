package com.makarytskyi.rentcar.dto.repairing

import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.model.Repairing.RepairingStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class UpdateRepairingRequest(
    @field:Min(0)
    @field:NotNull
    val price: Int,
    @field:NotNull
    val status: RepairingStatus,
) {

    fun toEntity(): Repairing = Repairing(
        carId = null,
        date = null,
        price = price,
        status = status,
    )
}
