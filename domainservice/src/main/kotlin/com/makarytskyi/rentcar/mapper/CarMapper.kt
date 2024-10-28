package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.dto.car.CarResponse
import com.makarytskyi.core.dto.car.CarResponse.CarColor
import com.makarytskyi.internalapi.model.car.Car
import com.makarytskyi.internalapi.model.car.CarColorProto
import com.makarytskyi.rentcar.model.MongoCar
import java.math.BigDecimal

fun CarResponse.toProto(): Car = Car.newBuilder()
    .setId(this.id)
    .setBrand(this.brand)
    .setModel(this.model)
    .setYear(this.year ?: 0)
    .setColor(this.color?.let { CarColorProto.valueOf(it.name) })
    .setPlate(this.plate)
    .setPrice(this.price.toDouble())
    .build()

fun MongoCar.toResponse(): CarResponse = CarResponse(
    id = id?.toString().orEmpty(),
    brand = brand.orEmpty(),
    model = model.orEmpty(),
    price = price ?: BigDecimal.ZERO,
    year = year,
    plate = plate.orEmpty(),
    color = color?.name?.let { CarColor.valueOf(it) },
)
