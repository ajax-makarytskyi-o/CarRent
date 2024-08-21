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

    companion object {
        fun toResponse(order: Order, carPrice: Int?): OrderResponse {
            val daysBetween = ChronoUnit.DAYS.between(
                order.from?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                order.to?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            )

            val calculatedPrice = carPrice?.let { daysBetween.times(it) } ?: 0

            return OrderResponse(
                order.id!!,
                order.carId ?: "none",
                order.userId ?: "none",
                order.from,
                order.to,
                price = calculatedPrice,
            )
        }
    }
}
