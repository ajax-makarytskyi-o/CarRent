package com.makarytskyi.rentcar.dto.order

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.model.aggregated.AggregatedMongoOrder
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date

data class AggregatedOrderResponse(
    val id: String,
    val car: CarResponse?,
    val user: UserResponse?,
    val from: Date?,
    val to: Date?,
    val price: Long?
) {

    companion object {
        fun from(mongoOrder: AggregatedMongoOrder): AggregatedOrderResponse {
            val daysBetween = ChronoUnit.DAYS.between(
                mongoOrder.from?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                mongoOrder.to?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            )

            return AggregatedOrderResponse(
                mongoOrder.id?.toString().orEmpty(),
                mongoOrder.car?.let { CarResponse.from(it) },
                mongoOrder.user?.let { UserResponse.from(it) },
                mongoOrder.from,
                mongoOrder.to,
                price = mongoOrder.car?.price?.times(daysBetween) ?: 0,
            )
        }
    }
}
