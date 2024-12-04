package com.makarytskyi.rentcar.order.domain.create

import java.math.BigDecimal
import java.util.Date

data class CreateOrder(
    val carId: String,
    val userId: String,
    val from: Date,
    val to: Date,
    val price: BigDecimal?,
)
