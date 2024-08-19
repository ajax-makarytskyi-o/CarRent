package com.makarytskyi.rentcar.model

import com.makarytskyi.rentcar.dto.order.OrderResponse
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date

data class Order(
    val id: String? = null,
    val carId: String?,
    val userId: String?,
    val from: Date?,
    val to: Date?,
) {

    fun toResponse(carPrice: Int?): OrderResponse {
        val daysBetween = ChronoUnit.DAYS.between(
            from?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
            to?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        )

        val calculatedPrice = carPrice?.let { daysBetween.times(it) } ?: 0

        return OrderResponse(
            id ?: "none",
            carId ?: "none",
            userId ?: "none",
            from,
            to,
            price = calculatedPrice,
        )
    }
}
