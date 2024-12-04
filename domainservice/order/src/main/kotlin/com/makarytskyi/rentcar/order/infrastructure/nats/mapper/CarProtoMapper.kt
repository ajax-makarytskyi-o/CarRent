package com.makarytskyi.rentcar.order.infrastructure.nats.mapper

import com.makarytskyi.commonmodels.car.Car
import com.makarytskyi.commonmodels.car.Car.CarColor
import com.makarytskyi.rentcar.car.domain.DomainCar

fun DomainCar.toProto(): Car = Car.newBuilder()
    .also {
        it.id = id
        it.brand = brand
        it.model = model
        it.year = year ?: 0
        it.color = color.toProto()
        it.plate = plate
        it.price = price.toDouble()
    }
    .build()

fun DomainCar.CarColor.toProto(): CarColor =
    when (this) {
        DomainCar.CarColor.RED -> CarColor.CAR_COLOR_RED
        DomainCar.CarColor.GREEN -> CarColor.CAR_COLOR_GREEN
        DomainCar.CarColor.BLUE -> CarColor.CAR_COLOR_BLUE
        DomainCar.CarColor.BLACK -> CarColor.CAR_COLOR_BLACK
        DomainCar.CarColor.WHITE -> CarColor.CAR_COLOR_WHITE
        DomainCar.CarColor.GREY -> CarColor.CAR_COLOR_GREY
        DomainCar.CarColor.YELLOW -> CarColor.CAR_COLOR_YELLOW
        DomainCar.CarColor.UNSPECIFIED -> CarColor.CAR_COLOR_UNSPECIFIED
    }
