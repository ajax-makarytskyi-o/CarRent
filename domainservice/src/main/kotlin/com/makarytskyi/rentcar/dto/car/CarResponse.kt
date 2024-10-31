package com.makarytskyi.rentcar.dto.car

import com.makarytskyi.rentcar.model.MongoCar
import java.math.BigDecimal

data class CarResponse(
    val id: String,
    val brand: String,
    val model: String,
    val price: BigDecimal,
    val year: Int?,
    val plate: String,
    val color: MongoCar.CarColor?,
) {

    companion object {
        fun from(mongoCar: MongoCar): CarResponse = CarResponse(
            requireNotNull(mongoCar.id?.toString()) { "Car id is null" },
            mongoCar.brand.orEmpty(),
            mongoCar.model.orEmpty(),
            mongoCar.price ?: BigDecimal.ZERO,
            mongoCar.year,
            mongoCar.plate.orEmpty(),
            mongoCar.color,
        )
    }
}
