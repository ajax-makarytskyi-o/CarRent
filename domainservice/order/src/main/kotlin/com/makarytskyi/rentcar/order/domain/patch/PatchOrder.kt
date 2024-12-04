package com.makarytskyi.rentcar.order.domain.patch

import java.util.Date

data class PatchOrder(
    val from: Date?,
    val to: Date?,
)
