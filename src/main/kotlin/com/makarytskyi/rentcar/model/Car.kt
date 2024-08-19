package com.makarytskyi.rentcar.model

import com.makarytskyi.rentcar.dto.car.CarResponse

data class Car(
    val id: String? = null,
    val mark: String?,
    val model: String?,
    val price: Int?,
    val year: Int?,
    val plate: String?,
    var color: CarColor?,
) {

    enum class CarColor {
        RED, GREEN, BLUE, BLACK, WHITE, GREY, YELLOW;
    }

    fun toResponse(): CarResponse = CarResponse(
        id ?: "none",
        mark ?: "none",
        model ?: "none",
        price ?: 0,
        year,
        plate ?: "none",
        color,
    )
}
