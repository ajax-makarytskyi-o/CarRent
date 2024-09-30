package com.makarytskyi.rentcar.dto.order

import com.makarytskyi.rentcar.model.MongoOrder
import java.math.BigDecimal
import java.time.temporal.ChronoUnit
import java.util.Date

data class OrderResponse(
    val id: String,
    val carId: String,
    val userId: String,
    val from: Date?,
    val to: Date?,
    val price: BigDecimal?,
) {

    companion object {
        fun from(mongoOrder: MongoOrder, carPrice: BigDecimal?): OrderResponse {
            val daysBetween = mongoOrder.from?.toInstant()?.until(mongoOrder.to?.toInstant(), ChronoUnit.DAYS) ?: 0
            val calculatedPrice = carPrice?.multiply(daysBetween.toBigDecimal()) ?: BigDecimal.ZERO

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
