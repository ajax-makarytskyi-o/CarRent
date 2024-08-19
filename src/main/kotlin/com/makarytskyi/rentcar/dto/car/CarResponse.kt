package com.makarytskyi.rentcar.dto.car

import com.makarytskyi.rentcar.model.Car

data class CarResponse(
    val id: String,
    val mark: String,
    val model: String,
    val price: Int,
    val year: Int?,
    val plate: String,
    val color: Car.CarColor?,
)
