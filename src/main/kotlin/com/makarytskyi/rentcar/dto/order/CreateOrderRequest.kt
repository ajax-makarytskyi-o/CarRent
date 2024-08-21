package com.makarytskyi.rentcar.dto.order

import com.makarytskyi.rentcar.model.Order
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.Date

data class CreateOrderRequest(
    @field:NotBlank
    val carId: String,
    @field:NotBlank
    val userId: String,
    @field:NotNull
    val from: Date,
    @field:NotNull
    val to: Date,
) {

    companion object {
        fun toEntity(orderRequest: CreateOrderRequest) = Order(
            carId = orderRequest.carId,
            userId = orderRequest.userId,
            from = orderRequest.from,
            to = orderRequest.to,
        )
    }
}
