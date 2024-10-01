package com.makarytskyi.rentcar.dto.car

import com.makarytskyi.rentcar.model.MongoCar
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CreateCarRequest(
    @field:NotBlank
    @field:Size(max = 30)
    val brand: String,
    @field:NotBlank
    @field:Size(max = 30)
    val model: String,
    @field:Min(0)
    val price: BigDecimal,
    val year: Int?,
    @field:NotNull
    @field:Size(max = 12)
    val plate: String,
    val color: MongoCar.CarColor?,
) {

    companion object {
        fun toEntity(carRequest: CreateCarRequest): MongoCar = MongoCar(
            brand = carRequest.brand,
            model = carRequest.model,
            price = carRequest.price,
            year = carRequest.year,
            plate = carRequest.plate,
            color = carRequest.color,
        )
    }
}
