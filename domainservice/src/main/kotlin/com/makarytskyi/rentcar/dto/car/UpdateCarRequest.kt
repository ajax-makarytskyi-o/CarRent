package com.makarytskyi.rentcar.dto.car

import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.patch.MongoCarPatch
import jakarta.validation.constraints.Min
import java.math.BigDecimal

data class UpdateCarRequest(
    @field:Min(0)
    val price: BigDecimal?,
    val color: MongoCar.CarColor?,
) {

    companion object {
        fun toPatch(carRequest: UpdateCarRequest) = MongoCarPatch(
            price = carRequest.price,
            color = carRequest.color,
        )
    }
}
