package com.makarytskyi.rentcar.car.domain.patch

import com.makarytskyi.rentcar.car.domain.DomainCar
import java.math.BigDecimal

data class DomainCarPatch(
    val price: BigDecimal?,
    val color: DomainCar.CarColor?,
)
