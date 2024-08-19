package com.makarytskyi.rentcar.dto.order

import java.util.*

data class OrderResponse (
    val id: String?,
    val carId: String?,
    val userId: String?,
    val from: Date?,
    val to: Date?,
    val price: Long?
)
