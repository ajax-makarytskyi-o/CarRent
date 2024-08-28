package com.makarytskyi.rentcar.dto.car

import com.makarytskyi.rentcar.model.Car
import jakarta.validation.constraints.Min

data class UpdateCarRequest(
    @field:Min(0)
    val price: Int?,
    val color: Car.CarColor?,
) {

    companion object {
        fun toEntity(carRequest: UpdateCarRequest) = Car(
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
