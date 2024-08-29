package com.makarytskyi.rentcar.dto.order

import com.makarytskyi.rentcar.model.Order
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date

data class OrderResponse(
    val id: String,
    val carId: String,
    val userId: String,
    val from: Date?,
    val to: Date?,
    val price: Long?
) {

    companion object {
        fun from(order: Order, carPrice: Int?): OrderResponse {
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

