package com.makarytskyi.rentcar.dto.order

import com.makarytskyi.rentcar.model.Order
import java.util.*

data class OrderRequest (
    val carId: String?,
    val userId: String?,
    val from: Date?,
    val to: Date?
) {

    fun toEntity() = Order(
        null,
        carId ?: throw IllegalArgumentException("Car in order is null"),
        userId ?: throw IllegalArgumentException("User in order is null"),
        from ?: throw IllegalArgumentException("Start time in order is null"),
        to ?: throw IllegalArgumentException("End time in order is null"),
    )
}
