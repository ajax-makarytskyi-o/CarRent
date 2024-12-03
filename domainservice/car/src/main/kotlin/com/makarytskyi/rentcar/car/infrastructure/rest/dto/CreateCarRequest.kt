package com.makarytskyi.rentcar.car.infrastructure.rest.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CreateCarRequest(
    @field:NotBlank
    @field:Size(max = 30)
    val brand: String,
    @field:NotBlank
    @field:Size(max = 30)
    val model: String,
    @field:Min(0)
    val price: BigDecimal,
    val year: Int?,
    @field:NotNull
    @field:Size(max = 12)
    val plate: String,
    val color: CarColor = CarColor.UNSPECIFIED,
) {
    enum class CarColor {
        RED, GREEN, BLUE, BLACK, WHITE, GREY, YELLOW, UNSPECIFIED
    }
}
