package com.makarytskyi.rentcar.repairing.infrastructure.rest.dto

import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import jakarta.validation.constraints.Min
import java.math.BigDecimal

data class UpdateRepairingRequest(
    @field:Min(0)
    val price: BigDecimal?,
    val status: DomainRepairing.RepairingStatus?,
) {
    enum class RepairingStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }
}
