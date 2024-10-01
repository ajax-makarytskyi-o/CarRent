package com.makarytskyi.rentcar.dto.order

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.model.projection.AggregatedMongoOrder
import java.math.BigDecimal
import java.time.temporal.ChronoUnit
import java.util.Date

data class AggregatedOrderResponse(
    val id: String,
    val car: CarResponse,
    val user: UserResponse,
    val from: Date?,
    val to: Date?,
    val price: BigDecimal?,
) {

    companion object {
        fun from(mongoOrder: AggregatedMongoOrder): AggregatedOrderResponse {
            val daysBetween =
                mongoOrder.from?.toInstant()?.until(mongoOrder.to?.toInstant(), ChronoUnit.DAYS) ?: 0

            return AggregatedOrderResponse(
                mongoOrder.id?.toString().orEmpty(),
                CarResponse.from(mongoOrder.car ?: MongoCar()),
                UserResponse.from(mongoOrder.user ?: MongoUser()),
                mongoOrder.from,
                mongoOrder.to,
                price = mongoOrder.car?.price?.times(daysBetween.toBigDecimal()) ?: BigDecimal.ZERO,
            )
        }
    }
}
