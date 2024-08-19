package com.makarytskyi.rentcar.dto.order

import java.util.*

data class UpdateOrderRequest(
    val from: Date?,
    val to: Date?
)
