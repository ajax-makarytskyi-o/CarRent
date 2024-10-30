package com.makarytskyi.gateway.fixtures

import com.makarytskyi.gateway.fixtures.Utils.generateString
import com.makarytskyi.internalapi.commonmodels.car.Car
import com.makarytskyi.internalapi.commonmodels.car.CarColorProto
import kotlin.random.Random
import org.bson.types.ObjectId

object CarProtoFixture {
    fun randomCar(): Car = Car.newBuilder()
        .apply {
            setId(ObjectId().toString())
            setBrand(generateString(15))
            setModel(generateString(15))
            setYear(Random.nextInt(1900, 2020))
            setColor(CarColorProto.CAR_COLOR_PROTO_RED)
            setPrice(Random.nextDouble(100.0, 500.0))
        }
        .build()
}
