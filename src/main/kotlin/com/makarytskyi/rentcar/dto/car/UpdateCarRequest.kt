package com.makarytskyi.rentcar.dto.car

import com.makarytskyi.rentcar.model.Car
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class UpdateCarRequest(
    @field:NotNull
    @field:Min(0)
    val price: Int,
    @field:NotNull
    val color: Car.CarColor,
)
