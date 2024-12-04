package com.makarytskyi.rentcar.car.domain.create

import com.makarytskyi.rentcar.car.domain.DomainCar.CarColor
import java.math.BigDecimal

data class CreateCar(
    val brand: String,
    val model: String,
    val price: BigDecimal,
    val year: Int?,
    val plate: String,
    val color: CarColor,
)
