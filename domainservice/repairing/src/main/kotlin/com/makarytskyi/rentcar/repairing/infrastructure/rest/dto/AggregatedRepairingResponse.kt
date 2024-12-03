package com.makarytskyi.rentcar.repairing.infrastructure.rest.dto

import com.makarytskyi.rentcar.car.infrastructure.rest.dto.CarResponse
import java.math.BigDecimal
import java.util.Date

data class AggregatedRepairingResponse(
    val id: String,
    val car: CarResponse,
    val date: Date?,
    val price: BigDecimal?,
    val status: RepairingResponse.RepairingStatus?,
)
