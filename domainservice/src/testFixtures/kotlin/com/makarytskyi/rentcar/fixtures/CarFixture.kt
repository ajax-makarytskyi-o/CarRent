package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.core.dto.car.CarResponseDto
import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.fixtures.Utils.generateString
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.patch.MongoCarPatch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random
import org.bson.types.ObjectId

object CarFixture {

    fun randomBrand(): String {
        return generateString(15)
    }

    fun randomModel(): String {
        return generateString(15)
    }

    fun randomYear(): Int {
        return Random.nextInt(1900, 2020)
    }

    fun randomPlate(): String {
        return generateString(6)
    }

    fun randomColor(): MongoCar.CarColor {
        return MongoCar.CarColor.entries.toTypedArray().random()
    }

    fun randomPrice(): BigDecimal {
        val min = BigDecimal.ZERO
        val max = BigDecimal("5000")
        val range = max.subtract(min)
        val randomFraction = BigDecimal(Random.nextDouble())
        val randomValue = min.add(range.multiply(randomFraction))
        return randomValue.setScale(2, RoundingMode.HALF_UP)
    }

    fun randomCar() = MongoCar(
        id = ObjectId(),
        brand = randomBrand(),
        model = randomModel(),
        price = randomPrice(),
        year = randomYear(),
        plate = randomPlate(),
        color = randomColor()
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
        brand = randomBrand(),
        model = randomModel(),
        price = randomPrice(),
        year = randomYear(),
        plate = randomPlate(),
        color = randomColor(),
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
        color = randomColor(),
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

    fun dtoColor(color: MongoCar.CarColor) = when (color) {
        MongoCar.CarColor.BLUE -> CarResponseDto.CarColor.BLUE
        MongoCar.CarColor.WHITE -> CarResponseDto.CarColor.WHITE
        MongoCar.CarColor.RED -> CarResponseDto.CarColor.RED
        MongoCar.CarColor.GREY -> CarResponseDto.CarColor.GREY
        MongoCar.CarColor.GREEN -> CarResponseDto.CarColor.GREEN
        MongoCar.CarColor.YELLOW -> CarResponseDto.CarColor.YELLOW
        MongoCar.CarColor.BLACK -> CarResponseDto.CarColor.BLACK
        MongoCar.CarColor.UNSPECIFIED -> CarResponseDto.CarColor.UNSPECIFIED
    }
}
