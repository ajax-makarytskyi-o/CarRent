package com.makarytskyi.core.dto.order

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.Date

data class CreateOrderRequestDto(
    @field:NotBlank
    val carId: String,
    @field:NotBlank
    val userId: String,
    @field:NotNull
    val from: Date,
    @field:NotNull
    val to: Date,
)
