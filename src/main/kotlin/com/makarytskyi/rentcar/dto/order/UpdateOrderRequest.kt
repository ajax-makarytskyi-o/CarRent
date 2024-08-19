package com.makarytskyi.rentcar.dto.order

import jakarta.validation.constraints.NotNull
import java.util.Date

data class UpdateOrderRequest(
    @field:NotNull
    val from: Date,
    @field:NotNull
    val to: Date,
)
