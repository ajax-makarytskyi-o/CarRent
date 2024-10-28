package com.makarytskyi.rentcar.model.patch

import com.makarytskyi.rentcar.model.MongoCar
import java.math.BigDecimal

data class MongoCarPatch(
    val price: BigDecimal?,
    val color: MongoCar.CarColor?,
)
