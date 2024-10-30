package com.makarytskyi.gateway.mapper

import com.makarytskyi.core.dto.car.CarResponseDto
import com.makarytskyi.core.dto.car.CarResponseDto.CarColor
import com.makarytskyi.internalapi.commonmodels.car.Car
import com.makarytskyi.internalapi.commonmodels.car.CarColorProto

fun Car.toResponse(): CarResponseDto = CarResponseDto(
    id = id,
    brand = brand,
    model = model,
    price = price.toBigDecimal(),
    year = year,
    plate = plate,
    color = color.toDto(),
)

fun CarColorProto.toDto(): CarColor =
    when (this) {
        CarColorProto.CAR_COLOR_PROTO_UNSPECIFIED -> CarColor.UNSPECIFIED
        CarColorProto.CAR_COLOR_PROTO_RED -> CarColor.RED
        CarColorProto.CAR_COLOR_PROTO_GREEN -> CarColor.GREEN
        CarColorProto.CAR_COLOR_PROTO_BLUE -> CarColor.BLUE
        CarColorProto.CAR_COLOR_PROTO_WHITE -> CarColor.WHITE
        CarColorProto.CAR_COLOR_PROTO_GREY -> CarColor.GREY
        CarColorProto.CAR_COLOR_PROTO_YELLOW -> CarColor.YELLOW
        CarColorProto.CAR_COLOR_PROTO_BLACK -> CarColor.BLACK
        CarColorProto.UNRECOGNIZED -> CarColor.UNSPECIFIED
    }
