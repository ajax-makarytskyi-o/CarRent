package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.domain.patch.DomainCarPatch
import com.makarytskyi.rentcar.car.infrastructure.mongo.entity.MongoCar
import com.makarytskyi.rentcar.car.infrastructure.mongo.mapper.toMongo
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.CarResponse
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.CreateCarRequest
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.UpdateCarRequest
import com.makarytskyi.rentcar.car.infrastructure.rest.mapper.toDomain
import com.makarytskyi.rentcar.car.infrastructure.rest.mapper.toResponse
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
        color = randomColor(),
    )

    fun randomMongoCar() = MongoCar(
        id = ObjectId(),
        brand = randomBrand(),
        model = randomModel(),
        price = randomPrice(),
        year = randomYear(),
        plate = randomPlate(),
        color = randomColor().toMongo(),
    )

    fun responseCar(car: DomainCar) = CarResponse(
        car.id.toString(),
        car.brand,
        car.model,
        car.price,
        car.year,
        car.plate,
        car.color.toResponse(),
    )

    fun createCarDtoRequest() = CreateCarRequest(
        brand = randomBrand(),
        model = randomModel(),
        price = randomPrice(),
        year = randomYear(),
        plate = randomPlate(),
        color = CreateCarRequest.CarColor.entries.random(),
    )

    fun createCarRequest(request: CreateCarRequest) = DomainCar(
        id = null,
        brand = request.brand,
        model = request.model,
        price = request.price,
        year = request.year,
        plate = request.plate,
        color = request.color.toDomain(),
    )

    fun updateCarDtoRequest() = UpdateCarRequest(
        price = randomPrice(),
        color = UpdateCarRequest.CarColor.entries.random(),
    )

    fun updateCarRequest(request: UpdateCarRequest) = DomainCarPatch(
        price = request.price,
        color = request.color?.toDomain(),
    )

    fun randomCreateCarRequest() = DomainCar(
        id = null,
        brand = randomBrand(),
        model = randomModel(),
        price = randomPrice(),
        year = randomYear(),
        plate = randomPlate(),
        color = randomColor(),
    )

    fun createdCar(car: DomainCar) = car.copy(id = ObjectId().toString())

    fun updateCarRequest() = DomainCarPatch(
        price = randomPrice(),
        color = randomColor(),
    )

    fun updateDomainCar(patch: DomainCarPatch, oldCar: DomainCar) = oldCar.copy(
        price = patch.price ?: oldCar.price,
        color = patch.color ?: oldCar.color,
    )

    fun emptyCarPatch() = DomainCarPatch(
        price = null,
        color = null,
    )

    fun updatedCar(oldCar: DomainCar, request: DomainCarPatch) =
        oldCar.copy(
            price = request.price ?: oldCar.price,
            color = request.color ?: oldCar.color,
        )
}
