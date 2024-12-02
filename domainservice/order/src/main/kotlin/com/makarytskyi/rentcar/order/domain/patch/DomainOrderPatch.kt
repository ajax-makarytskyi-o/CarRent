package com.makarytskyi.rentcar.order.domain.patch

import java.util.Date

data class DomainOrderPatch(
    val from: Date?,
    val to: Date?,
)
