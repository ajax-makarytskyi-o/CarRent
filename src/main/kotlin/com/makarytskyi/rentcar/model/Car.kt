package com.makarytskyi.rentcar.model

import com.makarytskyi.rentcar.dto.car.CarResponse

data class Car(
    val id: String? = null,
    val brand: String?,
    val model: String?,
    val price: Int?,
    val year: Int?,
    val plate: String?,
    var color: CarColor?,
) {

    enum class CarColor {
        RED, GREEN, BLUE, BLACK, WHITE, GREY, YELLOW;
    }

    companion object {
        fun toResponse(car: Car): CarResponse = CarResponse(
            car.id!!,
            car.brand ?: "none",
            car.model ?: "none",
            car.price ?: 0,
            car.year,
            car.plate ?: "none",
            car.color,
        )
    }
}
