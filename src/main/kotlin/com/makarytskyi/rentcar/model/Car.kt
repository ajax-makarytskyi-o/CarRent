package com.makarytskyi.rentcar.model

import com.makarytskyi.rentcar.dto.car.CarResponse
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Car(
    val id: String?,
    val mark: String?,
    val model: String?,
    val price: Int?,
    val year: Int?,
    val plate: String?,
    var color: CarColor?
) {

    enum class CarColor {
        RED, GREEN, BLUE, BLACK, WHITE, GREY, YELLOW;
    }

    fun toResponse(): CarResponse = CarResponse(
        id,
        mark ?: throw IllegalArgumentException("Mark of car is null"),
        model ?: throw IllegalArgumentException("Model of car is null"),
        price ?: throw IllegalArgumentException("Price of car is null"),
        year,
        plate ?: throw IllegalArgumentException("Plate of car is null"),
        color
    )
}
