package com.makarytskyi.core.dto.order

import java.util.Date

data class UpdateOrderRequest(
    val from: Date?,
    val to: Date?,
)
