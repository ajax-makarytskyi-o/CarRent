package com.makarytskyi.gateway.mapper

import com.makarytskyi.commonmodels.car.Car
import com.makarytskyi.commonmodels.car.Car.CarColor
import com.makarytskyi.core.dto.car.CarResponseDto


fun Car.toResponse(): CarResponseDto = CarResponseDto(
    id = id,
    brand = brand,
    model = model,
    price = price.toBigDecimal(),
    year = year,
    plate = plate,
    color = color.toDto(),
)

fun CarColor.toDto(): CarResponseDto.CarColor =
    when (this) {
        CarColor.CAR_COLOR_RED -> CarResponseDto.CarColor.RED
        CarColor.CAR_COLOR_GREEN -> CarResponseDto.CarColor.GREEN
        CarColor.CAR_COLOR_BLUE -> CarResponseDto.CarColor.BLUE
        CarColor.CAR_COLOR_WHITE -> CarResponseDto.CarColor.WHITE
        CarColor.CAR_COLOR_GREY -> CarResponseDto.CarColor.GREY
        CarColor.CAR_COLOR_YELLOW -> CarResponseDto.CarColor.YELLOW
        CarColor.CAR_COLOR_BLACK -> CarResponseDto.CarColor.BLACK
        CarColor.UNRECOGNIZED, CarColor.CAR_COLOR_UNSPECIFIED -> CarResponseDto.CarColor.UNSPECIFIED
    }
