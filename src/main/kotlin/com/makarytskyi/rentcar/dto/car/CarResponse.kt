package com.makarytskyi.rentcar.dto.car

import com.makarytskyi.rentcar.model.Car

data class CarResponse(
    val id: String,
    val brand: String,
    val model: String,
    val price: Int,
    val year: Int?,
    val plate: String,
    val color: Car.CarColor?,
) {

    companion object {
        fun from(car: Car): CarResponse = CarResponse(
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
