package com.makarytskyi.rentcar.dto.car

import com.makarytskyi.rentcar.model.Car

data class CreateCarRequest(
    val mark: String?,
    val model: String?,
    val price: Int?,
    val year: Int?,
    val plate: String?,
    val color: Car.CarColor?
) {

    fun toEntity(): Car = Car(
        null,
        mark ?: throw IllegalArgumentException("Mark of car is null"),
        model ?: throw IllegalArgumentException("Model of car is null"),
        price ?: throw IllegalArgumentException("Price of car is null"),
        year,
        plate ?: throw IllegalArgumentException("Plate of car is null"),
        color
    )
}
