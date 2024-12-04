package com.makarytskyi.rentcar.repairing.domain

import com.makarytskyi.rentcar.repairing.domain.patch.PatchRepairing
import java.math.BigDecimal
import java.util.Date

data class DomainRepairing(
    val id: String,
    val carId: String,
    val date: Date,
    val price: BigDecimal,
    val status: RepairingStatus,
) {
    enum class RepairingStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }

    fun fromPatch(patch: PatchRepairing): DomainRepairing = this.copy(
        price = patch.price ?: this.price,
        status = patch.status ?: this.status,
    )
}
