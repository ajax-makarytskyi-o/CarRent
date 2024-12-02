package com.makarytskyi.rentcar.repairing.domain.projection

import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import java.math.BigDecimal
import java.util.Date

data class AggregatedDomainRepairing(
    val id: String?,
    val car: DomainCar,
    val date: Date,
    val price: BigDecimal,
    val status: DomainRepairing.RepairingStatus,
)
