package com.makarytskyi.rentcar.model

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
}
