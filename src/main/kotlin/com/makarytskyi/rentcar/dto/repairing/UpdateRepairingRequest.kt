package com.makarytskyi.rentcar.dto.repairing

import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.MongoRepairing.RepairingStatus
import jakarta.validation.constraints.Min

data class UpdateRepairingRequest(
    @field:Min(0)
    val price: Int?,
    val status: RepairingStatus?,
) {

    companion object {
        fun toEntity(repairingRequest: UpdateRepairingRequest): MongoRepairing = MongoRepairing(
            carId = null,
            date = null,
            price = repairingRequest.price,
            status = repairingRequest.status,
        )
    }
}
