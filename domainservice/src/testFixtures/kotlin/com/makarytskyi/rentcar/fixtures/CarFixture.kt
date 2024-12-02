package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.core.dto.car.CarResponseDto
import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.domain.patch.DomainCarPatch
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.CarResponse
import com.makarytskyi.rentcar.fixtures.Utils.generateString
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

    fun randomColor(): DomainCar.CarColor {
        return DomainCar.CarColor.entries.toTypedArray().random()
    }

    fun randomPrice(): BigDecimal {
        val min = BigDecimal.ZERO
        val max = BigDecimal("5000")
        val range = max.subtract(min)
        val randomFraction = BigDecimal(Random.nextDouble())
        val randomValue = min.add(range.multiply(randomFraction))
        return randomValue.setScale(2, RoundingMode.HALF_UP)
    }

    fun randomCar() = DomainCar(
        id = ObjectId().toString(),
        brand = randomBrand(),
        model = randomModel(),
        price = randomPrice(),
        year = randomYear(),
        plate = randomPlate(),
        color = randomColor()
    )

    fun responseCar(mongoCar: DomainCar) = CarResponse(
        mongoCar.id.toString(),
        mongoCar.brand,
        mongoCar.model,
        mongoCar.price,
        mongoCar.year,
        mongoCar.plate,
        mongoCar.color,
    )

    fun createCarRequest() = DomainCar(
        id = null,
        brand = randomBrand(),
        model = randomModel(),
        price = randomPrice(),
        year = randomYear(),
        plate = randomPlate(),
        color = randomColor(),
    )

    fun createdCar(mongoCar: DomainCar) = mongoCar.copy(id = ObjectId().toString())

    fun updateCarRequest() = DomainCarPatch(
        price = randomPrice(),
        color = randomColor(),
    )

    fun updateDomainCar(patch: DomainCarPatch, oldCar: DomainCar) = oldCar.copy(
        price = patch.price ?: oldCar.price,
        color = patch.color ?: oldCar.color,
    )

    fun carPatch(request: DomainCarPatch) = DomainCarPatch(
        price = request.price,
        color = request.color,
    )

    fun emptyCarPatch() = DomainCarPatch(
        price = null,
        color = null,
    )

    fun updatedCar(oldMongoCar: DomainCar, request: DomainCarPatch) =
        oldMongoCar.copy(price = request.price ?: oldMongoCar.price, color = request.color ?: oldMongoCar.color)

    fun dtoColor(color: DomainCar.CarColor) = when (color) {
        DomainCar.CarColor.BLUE -> CarResponseDto.CarColor.BLUE
        DomainCar.CarColor.WHITE -> CarResponseDto.CarColor.WHITE
        DomainCar.CarColor.RED -> CarResponseDto.CarColor.RED
        DomainCar.CarColor.GREY -> CarResponseDto.CarColor.GREY
        DomainCar.CarColor.GREEN -> CarResponseDto.CarColor.GREEN
        DomainCar.CarColor.YELLOW -> CarResponseDto.CarColor.YELLOW
        DomainCar.CarColor.BLACK -> CarResponseDto.CarColor.BLACK
        DomainCar.CarColor.UNSPECIFIED -> CarResponseDto.CarColor.UNSPECIFIED
    }
}
