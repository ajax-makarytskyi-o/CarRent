package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.dto.car.CarResponseDto
import com.makarytskyi.internalapi.commonmodels.car.Car
import com.makarytskyi.internalapi.commonmodels.car.CarColor
import com.makarytskyi.rentcar.model.MongoCar
import java.math.BigDecimal

fun CarResponseDto.toProto(): Car = Car.newBuilder()
    .also {
        it.setId(this.id)
        it.setBrand(this.brand)
        it.setModel(this.model)
        it.setYear(this.year)
        it.setColor(this.color.toProto())
        it.setPlate(this.plate)
        it.setPrice(this.price.toDouble())
    }
    .build()

fun MongoCar.toResponse(): CarResponseDto = CarResponseDto(
    id = requireNotNull(id?.toString()) { "Car id is null" },
    brand = brand.orEmpty(),
    model = model.orEmpty(),
    price = price ?: BigDecimal.ZERO,
    year = year ?: 0,
    plate = plate.orEmpty(),
    color = color?.toResponse() ?: CarResponseDto.CarColor.UNSPECIFIED,
)

fun MongoCar.CarColor.toResponse(): CarResponseDto.CarColor =
    when (this) {
        MongoCar.CarColor.RED -> CarResponseDto.CarColor.RED
        MongoCar.CarColor.GREEN -> CarResponseDto.CarColor.GREEN
        MongoCar.CarColor.BLUE -> CarResponseDto.CarColor.BLUE
        MongoCar.CarColor.BLACK -> CarResponseDto.CarColor.BLACK
        MongoCar.CarColor.WHITE -> CarResponseDto.CarColor.WHITE
        MongoCar.CarColor.GREY -> CarResponseDto.CarColor.GREY
        MongoCar.CarColor.YELLOW -> CarResponseDto.CarColor.YELLOW
        MongoCar.CarColor.UNSPECIFIED -> CarResponseDto.CarColor.UNSPECIFIED
    }

private fun CarResponseDto.CarColor.toProto(): CarColor =
    when (this) {
        CarResponseDto.CarColor.RED -> CarColor.CAR_COLOR_RED
        CarResponseDto.CarColor.GREEN -> CarColor.CAR_COLOR_GREEN
        CarResponseDto.CarColor.BLUE -> CarColor.CAR_COLOR_BLUE
        CarResponseDto.CarColor.BLACK -> CarColor.CAR_COLOR_BLACK
        CarResponseDto.CarColor.WHITE -> CarColor.CAR_COLOR_WHITE
        CarResponseDto.CarColor.GREY -> CarColor.CAR_COLOR_GREY
        CarResponseDto.CarColor.YELLOW -> CarColor.CAR_COLOR_YELLOW
        CarResponseDto.CarColor.UNSPECIFIED -> CarColor.CAR_COLOR_UNSPECIFIED
    }
