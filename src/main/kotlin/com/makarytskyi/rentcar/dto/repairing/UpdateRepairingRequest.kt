package com.makarytskyi.rentcar.dto.repairing

import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.model.Repairing.RepairingStatus
import jakarta.validation.constraints.Min

data class UpdateRepairingRequest(
    @field:Min(0)
    val price: Int?,
    val status: RepairingStatus?,
) {

    companion object {
        fun toEntity(repairingRequest: UpdateRepairingRequest): Repairing = Repairing(
            carId = null,
            date = null,
            price = repairingRequest.price,
            status = repairingRequest.status,
        )
    }
}
