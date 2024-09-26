package com.makarytskyi.rentcar.dto.order

import com.makarytskyi.rentcar.model.MongoOrder
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
        fun from(mongoOrder: MongoOrder, carPrice: Int?): OrderResponse {
            val daysBetween = ChronoUnit.DAYS.between(
                mongoOrder.from?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                mongoOrder.to?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            )

            val calculatedPrice = carPrice?.let { daysBetween.times(it) } ?: 0

            return OrderResponse(
                mongoOrder.id?.toString().orEmpty(),
                mongoOrder.carId?.toString().orEmpty(),
                mongoOrder.userId?.toString().orEmpty(),
                mongoOrder.from,
                mongoOrder.to,
                price = calculatedPrice,
            )
        }
    }
}

