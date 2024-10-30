package com.makarytskyi.core.dto.order

import com.makarytskyi.core.dto.car.CarResponseDto
import com.makarytskyi.core.dto.user.UserResponseDto
import java.math.BigDecimal
import java.util.Date

data class AggregatedOrderResponseDto(
    val id: String,
    val car: CarResponseDto,
    val user: UserResponseDto,
    val from: Date,
    val to: Date,
    val price: BigDecimal,
)
