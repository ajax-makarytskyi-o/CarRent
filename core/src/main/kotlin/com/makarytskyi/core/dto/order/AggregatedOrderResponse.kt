package com.makarytskyi.core.dto.order

import com.makarytskyi.core.dto.car.CarResponse
import com.makarytskyi.core.dto.user.UserResponse
import java.math.BigDecimal
import java.util.Date

data class AggregatedOrderResponse(
    val id: String,
    val car: CarResponse,
    val user: UserResponse,
    val from: Date?,
    val to: Date?,
    val price: BigDecimal?,
)
