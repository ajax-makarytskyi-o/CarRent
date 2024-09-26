package com.makarytskyi.rentcar.dto.car

import com.makarytskyi.rentcar.model.MongoCar

data class CarResponse(
    val id: String,
    val brand: String,
    val model: String,
    val price: Int,
    val year: Int?,
    val plate: String,
    val color: MongoCar.CarColor?,
) {

    companion object {
        fun from(mongoCar: MongoCar): CarResponse = CarResponse(
            mongoCar.id?.toString().orEmpty(),
            mongoCar.brand.orEmpty(),
            mongoCar.model.orEmpty(),
            mongoCar.price ?: 0,
            mongoCar.year,
            mongoCar.plate.orEmpty(),
            mongoCar.color,
        )
    }
}
