package com.makarytskyi.rentcar.model

import java.util.Date

data class Order(
    val id: String? = null,
    val carId: String?,
    val userId: String?,
    val from: Date?,
    val to: Date?,
)
