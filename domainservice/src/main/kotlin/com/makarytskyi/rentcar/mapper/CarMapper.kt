package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.dto.car.CarResponseDto
import com.makarytskyi.core.dto.car.CarResponseDto.CarColor
import com.makarytskyi.internalapi.commonmodels.car.Car
import com.makarytskyi.internalapi.commonmodels.car.CarColorProto
import com.makarytskyi.rentcar.model.MongoCar
import java.math.BigDecimal

fun CarResponseDto.toProto(): Car = Car.newBuilder()
    .apply {
        setId(this@toProto.id)
        setBrand(this@toProto.brand)
        setModel(this@toProto.model)
        setYear(this@toProto.year)
        setColor(this@toProto.color.toProto())
        setPlate(this@toProto.plate)
        setPrice(this@toProto.price.toDouble())
    }
    .build()


fun MongoCar.toResponse(): CarResponseDto = CarResponseDto(
    id = id?.toString().orEmpty(),
    brand = brand.orEmpty(),
    model = model.orEmpty(),
    price = price ?: BigDecimal.ZERO,
    year = year ?: 0,
    plate = plate.orEmpty(),
    color = color?.toResponse() ?: CarColor.UNSPECIFIED,
)

fun MongoCar.CarColor.toResponse(): CarColor =
    when (this) {
        MongoCar.CarColor.RED -> CarColor.RED
        MongoCar.CarColor.GREEN -> CarColor.GREEN
        MongoCar.CarColor.BLUE -> CarColor.BLUE
        MongoCar.CarColor.BLACK -> CarColor.BLACK
        MongoCar.CarColor.WHITE -> CarColor.WHITE
        MongoCar.CarColor.GREY -> CarColor.GREY
        MongoCar.CarColor.YELLOW -> CarColor.YELLOW
        MongoCar.CarColor.UNSPECIFIED -> CarColor.UNSPECIFIED
    }

private fun CarColor.toProto(): CarColorProto =
    when (this) {
        CarColor.RED -> CarColorProto.CAR_COLOR_PROTO_RED
        CarColor.GREEN -> CarColorProto.CAR_COLOR_PROTO_GREEN
        CarColor.BLUE -> CarColorProto.CAR_COLOR_PROTO_BLUE
        CarColor.BLACK -> CarColorProto.CAR_COLOR_PROTO_BLACK
        CarColor.WHITE -> CarColorProto.CAR_COLOR_PROTO_WHITE
        CarColor.GREY -> CarColorProto.CAR_COLOR_PROTO_GREY
        CarColor.YELLOW -> CarColorProto.CAR_COLOR_PROTO_YELLOW
        CarColor.UNSPECIFIED -> CarColorProto.CAR_COLOR_PROTO_UNSPECIFIED
    }
