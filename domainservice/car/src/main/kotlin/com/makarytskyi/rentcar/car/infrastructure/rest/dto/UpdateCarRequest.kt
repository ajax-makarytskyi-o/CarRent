package com.makarytskyi.rentcar.car.infrastructure.rest.dto

import com.makarytskyi.rentcar.car.domain.DomainCar
import jakarta.validation.constraints.Min
import java.math.BigDecimal

data class UpdateCarRequest(
    @field:Min(0)
    val price: BigDecimal?,
    val color: DomainCar.CarColor?,
)
