package com.makarytskyi.rentcar.car.infrastructure.rest.dto

import jakarta.validation.constraints.Min
import java.math.BigDecimal

data class UpdateCarRequest(
    @field:Min(0)
    val price: BigDecimal?,
    val color: CarColor?,
) {
    enum class CarColor {
        RED, GREEN, BLUE, BLACK, WHITE, GREY, YELLOW, UNSPECIFIED
    }
}
