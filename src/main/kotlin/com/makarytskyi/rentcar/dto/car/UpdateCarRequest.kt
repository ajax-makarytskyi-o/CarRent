package com.makarytskyi.rentcar.dto.car

import com.makarytskyi.rentcar.model.MongoCar
import jakarta.validation.constraints.Min
import java.math.BigDecimal

data class UpdateCarRequest(
    @field:Min(0)
    val price: BigDecimal?,
    val color: MongoCar.CarColor?,
) {

    companion object {
        fun toEntity(carRequest: UpdateCarRequest) = MongoCar(
            id = null,
            brand = null,
            model = null,
            price = carRequest.price,
            year = null,
            plate = null,
            color = carRequest.color,
        )
    }
}
