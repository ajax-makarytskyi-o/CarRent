package com.makarytskyi.core.dto.car

import java.math.BigDecimal

data class CarResponse(
    val id: String,
    val brand: String,
    val model: String,
    val price: BigDecimal,
    val year: Int?,
    val plate: String,
    val color: CarColor?,
) {
    enum class CarColor {
        RED, GREEN, BLUE, BLACK, WHITE, GREY, YELLOW
    }
}
