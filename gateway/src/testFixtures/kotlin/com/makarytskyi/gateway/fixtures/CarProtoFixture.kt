package com.makarytskyi.gateway.fixtures

import com.makarytskyi.core.dto.car.CarResponseDto
import com.makarytskyi.gateway.fixtures.Utils.generateString
import com.makarytskyi.internalapi.commonmodels.car.Car
import com.makarytskyi.internalapi.commonmodels.car.Car.CarColor
import kotlin.random.Random
import org.bson.types.ObjectId

object CarProtoFixture {
    fun randomCar(): Car = Car.newBuilder()
        .apply {
            setId(ObjectId().toString())
            setBrand(generateString(15))
            setModel(generateString(15))
            setYear(Random.nextInt(1900, 2020))
            setColor(CarColor.CAR_COLOR_RED)
            setPrice(Random.nextDouble(100.0, 500.0))
        }
        .build()

    fun dtoColor(color: CarColor) = when (color) {
        CarColor.CAR_COLOR_BLUE -> CarResponseDto.CarColor.BLUE
        CarColor.CAR_COLOR_WHITE -> CarResponseDto.CarColor.WHITE
        CarColor.CAR_COLOR_RED -> CarResponseDto.CarColor.RED
        CarColor.CAR_COLOR_GREY -> CarResponseDto.CarColor.GREY
        CarColor.CAR_COLOR_GREEN -> CarResponseDto.CarColor.GREEN
        CarColor.CAR_COLOR_YELLOW -> CarResponseDto.CarColor.YELLOW
        CarColor.CAR_COLOR_BLACK -> CarResponseDto.CarColor.BLACK
        CarColor.CAR_COLOR_UNSPECIFIED, CarColor.UNRECOGNIZED -> CarResponseDto.CarColor.UNSPECIFIED
    }
}
