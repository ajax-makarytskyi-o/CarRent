package com.makarytskyi.rentcar.repairing.infrastructure.rest.dto

import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import java.math.BigDecimal
import java.util.Date

data class RepairingResponse(
    val id: String,
    val carId: String,
    val date: Date?,
    val price: BigDecimal?,
    val status: DomainRepairing.RepairingStatus?,
)
