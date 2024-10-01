package com.makarytskyi.rentcar.dto.repairing

import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.MongoRepairing.RepairingStatus
import com.makarytskyi.rentcar.model.patch.MongoRepairingPatch
import jakarta.validation.constraints.Min
import java.math.BigDecimal

data class UpdateRepairingRequest(
    @field:Min(0)
    val price: BigDecimal?,
    val status: RepairingStatus?,
) {

    companion object {
        fun toPatch(repairingRequest: UpdateRepairingRequest) = MongoRepairingPatch(
            price = repairingRequest.price,
            status = repairingRequest.status,
        )
    }
}
