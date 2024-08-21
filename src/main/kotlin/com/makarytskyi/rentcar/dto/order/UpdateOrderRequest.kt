package com.makarytskyi.rentcar.dto.order

import com.makarytskyi.rentcar.model.Order
import jakarta.validation.constraints.NotNull
import java.util.Date

data class UpdateOrderRequest(
    val from: Date?,
    val to: Date?,
) {

    companion object {
        fun toEntity(orderRequest: UpdateOrderRequest) = Order(
            id = null,
            carId = null,
            userId = null,
            orderRequest.from,
            orderRequest.to,
        )
    }
}
