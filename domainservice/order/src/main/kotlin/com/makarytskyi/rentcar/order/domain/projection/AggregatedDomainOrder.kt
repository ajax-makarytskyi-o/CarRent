package com.makarytskyi.rentcar.order.domain.projection

import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.user.domain.DomainUser
import java.math.BigDecimal
import java.util.Date

data class AggregatedDomainOrder(
    val id: String,
    val car: DomainCar,
    val user: DomainUser,
    val from: Date,
    val to: Date,
    val price: BigDecimal?,
)
