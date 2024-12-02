package com.makarytskyi.rentcar.car.infrastructure.rest.dto

import com.makarytskyi.rentcar.car.domain.DomainCar
import java.math.BigDecimal

data class CarResponse(
    val id: String,
    val brand: String,
    val model: String,
    val price: BigDecimal,
    val year: Int?,
    val plate: String,
    val color: DomainCar.CarColor?,
)
