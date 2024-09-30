package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.MongoCar
import fixtures.CarFixture.randomCar
import java.math.BigDecimal
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class CarRepositoryTest : ContainerBase {

    @Autowired
    lateinit var carRepository: CarRepository

    @Test
    fun `create should insert car and return it with id`() {
        // GIVEN
        val unexistingCar = randomCar().copy(id = null)

        // WHEN
        val createdCar = carRepository.create(unexistingCar)

        // THEN
        val foundCar = carRepository.findById(createdCar.id.toString())
        assertEquals(createdCar, foundCar)
        assertNotNull(createdCar.id)
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
    fun `update should update car`() {
        // GIVEN
        val price = BigDecimal("600")
        val color = MongoCar.CarColor.BLUE
        val car = carRepository.create(randomCar())
        val updateCar = car.copy(
            price = price,
            color = color
        )

        // WHEN
        val updated = carRepository.update(updateCar.id.toString(), updateCar)

        // THENs
        assertEquals(price, updated?.price)
        assertEquals(color, updated?.color)
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
        assertEquals(car, foundCar)
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
        val car1 = carRepository.create(randomCar().copy(brand = brand))
        val car2 = carRepository.create(randomCar().copy(brand = brand))

        // WHEN
        val foundCars = carRepository.findAllByBrand(brand)

        // THEN
        assertTrue(foundCars.containsAll(listOf(car1, car2)))
    }

    @Test
    fun `findAllByBrandAndModel should find cars by brand and model`() {
        // GIVEN
        val brand = "brand"
        val model = "model"
        val car1 = carRepository.create(randomCar().copy(brand = brand, model = model))
        val car2 = carRepository.create(randomCar().copy(brand = brand, model = model))

        // WHEN
        val foundCars = carRepository.findAllByBrandAndModel(brand, model)

        // THEN
        assertTrue(foundCars.containsAll(listOf(car1, car2)))
    }

    @Test
    fun `findById should return existing car by id`() {
        // GIVEN
        val car = carRepository.create(randomCar())

        // WHEN
        val foundCar = carRepository.findById(car.id.toString())

        // THEN
        assertEquals(car, foundCar)
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

        // WHEN
        carRepository.deleteById(car.id.toString())

        // THEN
        val result = carRepository.findById(car.id.toString())
        assertNull(result)
    }
}
