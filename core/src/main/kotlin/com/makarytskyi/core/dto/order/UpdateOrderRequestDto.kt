package com.makarytskyi.core.dto.order

import java.util.Date

data class UpdateOrderRequestDto(
    val from: Date?,
    val to: Date?,
)
