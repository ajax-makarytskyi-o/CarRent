package com.makarytskyi.rentcar.repairing.domain.create

import com.makarytskyi.rentcar.repairing.domain.DomainRepairing.RepairingStatus
import java.math.BigDecimal
import java.util.Date

data class CreateRepairing(
    val carId: String,
    val date: Date,
    val price: BigDecimal,
    val status: RepairingStatus,
)
