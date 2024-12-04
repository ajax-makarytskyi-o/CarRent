package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.domain.create.CreateCar
import com.makarytskyi.rentcar.car.domain.patch.PatchCar
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
        car.id,
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

    fun createCarRequest() = CreateCar(
        brand = randomBrand(),
        model = randomModel(),
        price = randomPrice(),
        year = randomYear(),
        plate = randomPlate(),
        color = DomainCar.CarColor.entries.random(),
    )

    fun createCarRequest(request: CreateCarRequest) = CreateCar(
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

    fun updateCarRequest(request: UpdateCarRequest) = PatchCar(
        price = request.price,
        color = request.color?.toDomain(),
    )

    fun randomCreateCarRequest() = CreateCar(
        brand = randomBrand(),
        model = randomModel(),
        price = randomPrice(),
        year = randomYear(),
        plate = randomPlate(),
        color = randomColor(),
    )

    fun createdCar(car: CreateCar): DomainCar = DomainCar(
        id = ObjectId().toString(),
        brand = car.brand,
        model = car.model,
        price = car.price,
        year = car.year,
        plate = car.plate,
        color = car.color,
    )

    fun updateCarRequest() = PatchCar(
        price = randomPrice(),
        color = randomColor(),
    )

    fun updateDomainCar(patch: PatchCar, oldCar: DomainCar) = oldCar.copy(
        price = patch.price ?: oldCar.price,
        color = patch.color ?: oldCar.color,
    )

    fun emptyCarPatch() = PatchCar(
        price = null,
        color = null,
    )

    fun updatedCar(oldCar: DomainCar, request: PatchCar) =
        oldCar.copy(
            price = request.price ?: oldCar.price,
            color = request.color ?: oldCar.color,
        )
}
