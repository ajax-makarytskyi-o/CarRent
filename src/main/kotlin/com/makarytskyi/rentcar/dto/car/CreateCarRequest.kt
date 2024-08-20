package com.makarytskyi.rentcar.dto.car

import com.makarytskyi.rentcar.model.Car
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateCarRequest(
    @field:NotBlank
    @field:Size(max = 30)
    val mark: String,
    @field:NotBlank
    @field:Size(max = 30)
    val model: String?,
    @field:NotNull
    @field:Min(0)
    val price: Int,
    val year: Int?,
    @field:NotNull
    @field:Size(max = 12)
    val plate: String,
    val color: Car.CarColor?,
) {

    companion object {
        fun toEntity(carRequest: CreateCarRequest): Car = Car(
            mark = carRequest.mark,
            model = carRequest.model,
            price = carRequest.price,
            year = carRequest.year,
            plate = carRequest.plate,
            color = carRequest.color,
        )
    }
}
