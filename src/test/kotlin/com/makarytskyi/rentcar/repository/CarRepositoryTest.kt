package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.fixtures.CarFixture.emptyCarPatch
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.model.MongoCar
import java.math.BigDecimal
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

internal class CarRepositoryTest : ContainerBase {

    @Autowired
    lateinit var carRepository: CarRepository

    @Test
    fun `create should insert car and return it with id`() {
        // GIVEN
        val car = randomCar().copy(id = null)

        // WHEN
        val result = carRepository.create(car)

        // THEN
        result
            .test()
            .assertNext {
                assertNotNull(it.id, "Car should have non-null id after saving")
                assertEquals(car.copy(id = it.id), it)
            }
            .verifyComplete()
    }

    @Test
    fun `findAll should find all cars`() {
        // GIVEN
        val insertedCar1 = carRepository.create(randomCar()).block()
        val insertedCar2 = carRepository.create(randomCar()).block()

        // WHEN
        val cars = carRepository.findAll(0, 20)

        // THEN
        cars.collectList()
            .test()
            .assertNext {
                assertTrue(
                    it.containsAll(listOf(insertedCar1, insertedCar2)),
                    "Result should contain expected cars."
                )
            }
            .verifyComplete()
    }

    @Test
    fun `patch should partially update car`() {
        // GIVEN
        val price = BigDecimal("600")
        val color = MongoCar.CarColor.BLUE
        val car = carRepository.create(randomCar()).block()
        val updateCar = emptyCarPatch().copy(price = price, color = color)

        // WHEN
        val updated = carRepository.patch(car?.id.toString(), updateCar)

        // THEN
        updated
            .test()
            .assertNext {
                assertEquals(price, it.price)
                assertEquals(color, it.color)
            }
            .verifyComplete()
    }

    @Test
    fun `findByPlate should find existing car by plate`() {
        // GIVEN
        val plate = "XX5295YY"
        val car = randomCar().copy(plate = plate)
        carRepository.create(car).block()

        // WHEN
        val foundCar = carRepository.findByPlate(plate)

        // THEN
        foundCar
            .test()
            .assertNext {
                assertEquals(plate, it.plate)
            }
            .verifyComplete()
    }

    @Test
    fun `findByPlate should return empty if cant find car by plate`() {
        // GIVEN
        val unexistingPlate = "wrongPlate"

        // WHEN
        val foundCar = carRepository.findByPlate(unexistingPlate)

        // THEN
        foundCar
            .test()
            .verifyComplete()
    }

    @Test
    fun `findAllByBrand should find all cars by brand`() {
        // GIVEN
        val brand = "SomeBrand"
        val car1 = carRepository.create(randomCar().copy(brand = brand)).block()
        val car2 = carRepository.create(randomCar().copy(brand = brand)).block()

        // WHEN
        val foundCars = carRepository.findAllByBrand(brand)

        // THEN
        foundCars.collectList()
            .test()
            .assertNext {
                assertTrue(it.containsAll(listOf(car1, car2)), "Result should contain all expected cars.")
            }
            .verifyComplete()
    }

    @Test
    fun `findAllByBrandAndModel should find cars by brand and model`() {
        // GIVEN
        val brand = "brand"
        val model = "model"
        val car1 = carRepository.create(randomCar().copy(brand = brand, model = model)).block()
        val car2 = carRepository.create(randomCar().copy(brand = brand, model = model)).block()

        // WHEN
        val foundCars = carRepository.findAllByBrandAndModel(brand, model)

        // THEN
        foundCars.collectList()
            .test()
            .assertNext {
                assertTrue(it.containsAll(listOf(car1, car2)), "Result should contain all expected cars.")
            }
            .verifyComplete()
    }

    @Test
    fun `findById should return existing car by id`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()

        // WHEN
        val foundCar = carRepository.findById(car?.id.toString())

        // THEN
        foundCar
            .test()
            .expectNext(car)
            .verifyComplete()
    }

    @Test
    fun `findById should return empty if cant find car by id`() {
        // GIVEN
        val unexistingId = ObjectId().toString()

        // WHEN
        val foundCar = carRepository.findById(unexistingId)

        // THEN
        foundCar
            .test()
            .verifyComplete()
    }

    @Test
    fun `deleteById should delete car by id`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()

        // WHEN
        carRepository.deleteById(car?.id.toString()).block()

        // THEN
        carRepository.findById(car?.id.toString())
            .test()
            .verifyComplete()
    }
}
