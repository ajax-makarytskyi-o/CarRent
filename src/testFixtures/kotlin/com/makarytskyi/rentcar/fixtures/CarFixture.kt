package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.fixtures.Utils.generateString
import com.makarytskyi.rentcar.fixtures.Utils.randomPrice
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.patch.MongoCarPatch
import java.math.BigDecimal
import kotlin.random.Random
import org.bson.types.ObjectId

object CarFixture {
    fun randomCar() = MongoCar(
        id = ObjectId(),
        brand = generateString(15),
        model = generateString(15),
        price = randomPrice(),
        year = Random.nextInt(1900, 2020),
        plate = generateString(6),
        color = MongoCar.CarColor.BLUE
    )

    fun responseCar(mongoCar: MongoCar) = CarResponse(
        mongoCar.id.toString(),
        mongoCar.brand!!,
        mongoCar.model!!,
        mongoCar.price!!,
        mongoCar.year,
        mongoCar.plate!!,
        mongoCar.color,
    )

    fun emptyResponseCar() = CarResponse(
        id = "",
        brand = "",
        model = "",
        price = BigDecimal.ZERO,
        year = null,
        plate = "",
        color = null,
    )

    fun createCarRequest() = CreateCarRequest(
        brand = generateString(15),
        model = generateString(15),
        price = randomPrice(),
        year = Random.nextInt(1900, 2020),
        plate = generateString(6),
        color = MongoCar.CarColor.RED,
    )

    fun createCarEntity(request: CreateCarRequest) = MongoCar(
        id = null,
        brand = request.brand,
        model = request.model,
        price = request.price,
        year = request.year,
        plate = request.plate,
        color = request.color,
    )

    fun createdCar(mongoCar: MongoCar) = mongoCar.copy(id = ObjectId())

    fun updateCarRequest() = UpdateCarRequest(
        price = randomPrice(),
        color = MongoCar.CarColor.GREEN,
    )

    fun carPatch(request: UpdateCarRequest) = MongoCarPatch(
        price = request.price,
        color = request.color,
    )

    fun emptyCarPatch() = MongoCarPatch(
        price = null,
        color = null,
    )

    fun updatedCar(oldMongoCar: MongoCar, request: UpdateCarRequest) =
        oldMongoCar.copy(price = request.price, color = request.color)
}
