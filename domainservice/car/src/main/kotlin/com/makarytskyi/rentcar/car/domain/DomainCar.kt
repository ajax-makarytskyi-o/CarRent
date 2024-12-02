package com.makarytskyi.rentcar.car.domain

import com.makarytskyi.rentcar.car.domain.patch.DomainCarPatch
import java.math.BigDecimal

data class DomainCar(
    val id: String?,
    val brand: String,
    val model: String,
    val price: BigDecimal,
    val year: Int?,
    val plate: String,
    val color: CarColor,
) {
    enum class CarColor {
        RED, GREEN, BLUE, BLACK, WHITE, GREY, YELLOW, UNSPECIFIED
    }

    fun fromPatch(patch: DomainCarPatch): DomainCar = this.copy(
        price = patch.price ?: this.price,
        color = patch.color ?: this.color,
    )
}
