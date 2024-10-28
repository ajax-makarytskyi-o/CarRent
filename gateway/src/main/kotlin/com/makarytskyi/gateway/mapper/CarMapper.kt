package com.makarytskyi.gateway.mapper

import com.makarytskyi.core.dto.car.CarResponse
import com.makarytskyi.core.dto.car.CarResponse.CarColor
import com.makarytskyi.internalapi.model.car.Car

fun Car.toResponse(): CarResponse = CarResponse(
    id = id,
    brand = brand,
    model = model,
    price = price.toBigDecimal(),
    year = year,
    plate = plate,
    color = CarColor.valueOf(color.toString()),
)
