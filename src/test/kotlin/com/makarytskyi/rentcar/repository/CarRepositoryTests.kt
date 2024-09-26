package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.MongoCar
import fixtures.CarFixture.randomCar
import fixtures.CarFixture.unexistingCar
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CarRepositoryTests : ContainerBase {

    @Autowired
    lateinit var carRepository: CarRepository

    @Test
    fun `create should insert car and return it with id`() {
        // GIVEN
        val unexistingCar = unexistingCar()

        // WHEN
        val car = carRepository.create(unexistingCar)

        // THEN
        val foundCar = carRepository.findById(car.id.toString())
        assertNotNull(foundCar)
        assertNotNull(car.id)
    }

    @Test
    fun `findAll should find all cars`() {
        // GIVEN
        val insertedCar1 = carRepository.create(randomCar())
        val insertedCar2 = carRepository.create(randomCar())

        // WHEN
        val cars = carRepository.findAll()

        // THEN
        assertTrue(cars.any { it.brand == insertedCar1.brand && it.plate == insertedCar1.plate })
        assertTrue(cars.any { it.brand == insertedCar2.brand && it.plate == insertedCar2.plate })

    }

    @Test
    fun `update should update color of car`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val updateCar = car.copy(
            price = null,
            color = MongoCar.CarColor.BLUE
        )

        // WHEN
        carRepository.update(updateCar.id.toString(), updateCar)

        // THEN
        val updated = carRepository.findById(updateCar.id.toString())

        assertNotNull(updated)
        assertEquals(updateCar.color, updated?.color)
    }

    @Test
    fun `update should update price of car`() {
        // GIVEN
        val price = 600
        val car = carRepository.create(randomCar())
        val updateCar = car.copy(
            price = price,
            color = null
        )

        // WHEN
        carRepository.update(updateCar.id.toString(), updateCar)

        // THEN
        val updated = carRepository.findById(car.id.toString())

        assertNotNull(updated)
        assertEquals(price, updated?.price)
        assertEquals(car.color, updated?.color)
    }

    @Test
    fun `findByPlate should find existing car by plate`() {
        // GIVEN
        val plate = "XX5295YY"
        val car = randomCar().copy(plate = plate)
        carRepository.create(car)

        // WHEN
        val foundCar = carRepository.findByPlate(plate)

        // THEN
        assertNotNull(foundCar)
    }

    @Test
    fun `findByPlate should return null if cant find car by plate`() {
        // GIVEN
        val unexistingPlate = "wrongPlate"

        // WHEN
        val foundCar = carRepository.findByPlate(unexistingPlate)

        // THEN
        assertNull(foundCar)
    }

    @Test
    fun `findAllByBrand should find all cars by brand`() {
        // GIVEN
        val brand = "SomeBrand"
        carRepository.create(randomCar().copy(brand = brand))
        carRepository.create(randomCar().copy(brand = brand))

        // WHEN
        val foundCars = carRepository.findAllByBrand(brand)

        // THEN
        assertEquals(2, foundCars.size)
    }

    @Test
    fun `findAllByBrandAndModel should find cars by brand and model`() {
        // GIVEN
        val brand = "brand"
        val model = "model"
        carRepository.create(randomCar().copy(brand = brand, model = model))
        carRepository.create(randomCar().copy(brand = brand, model = model))

        // WHEN
        val foundCars = carRepository.findAllByBrandAndModel(brand, model)

        // THEN
        assertEquals(2, foundCars.size)
    }

    @Test
    fun `findById should return existing car by id`() {
        // GIVEN
        val car = carRepository.create(randomCar())

        // WHEN
        val foundCar = carRepository.findById(car.id.toString())

        // THEN
        assertNotNull(foundCar)
    }

    @Test
    fun `findById should return null if cant find car by id`() {
        // GIVEN
        val unexistingId = ObjectId().toString()

        // WHEN
        val foundCar = carRepository.findById(unexistingId)

        // THEN
        assertNull(foundCar)
    }

    @Test
    fun `deleteById should delete car by id`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val check = carRepository.findById(car.id.toString())

        // WHEN
        carRepository.deleteById(car.id.toString())

        // THEN
        val result = carRepository.findById(car.id.toString())
        assertNull(result)
        assertNotNull(check)
    }
}
