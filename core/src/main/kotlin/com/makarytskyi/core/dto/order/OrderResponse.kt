package com.makarytskyi.core.dto.order

import java.math.BigDecimal
import java.util.Date

data class OrderResponse(
    val id: String,
    val carId: String,
    val userId: String,
    val from: Date?,
    val to: Date?,
    val price: BigDecimal?,
)
