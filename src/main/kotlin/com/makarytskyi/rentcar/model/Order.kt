package com.makarytskyi.rentcar.model

import com.makarytskyi.rentcar.dto.order.OrderResponse
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

data class Order(
    val id: String?,
    val carId: String?,
    val userId: String?,
    val from: Date?,
    val to: Date?
) {

    fun toResponse(carPrice: Int?): OrderResponse {
        val daysBetween = ChronoUnit.DAYS.between(
            from?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
            to?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        )

        val calculatedPrice = carPrice?.let { daysBetween.times(it) } ?: 0

        return OrderResponse(
            id,
            carId ?: throw IllegalArgumentException("Car in order is null"),
            userId ?: throw IllegalArgumentException("User in order is null"),
            from ?: throw IllegalArgumentException("Start time in order is null"),
            to ?: throw IllegalArgumentException("End time in order is null"),
            price = calculatedPrice
        )
    }
}
