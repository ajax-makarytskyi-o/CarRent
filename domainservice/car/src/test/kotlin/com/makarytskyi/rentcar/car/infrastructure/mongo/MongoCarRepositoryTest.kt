package com.makarytskyi.rentcar.car.infrastructure.mongo

import com.makarytskyi.rentcar.car.ContainerBase
import com.makarytskyi.rentcar.car.application.port.output.CarRepositoryOutputPort
import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.fixtures.CarFixture.createCarRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.createdCar
import com.makarytskyi.rentcar.fixtures.CarFixture.emptyCarPatch
import com.makarytskyi.rentcar.fixtures.CarFixture.randomBrand
import com.makarytskyi.rentcar.fixtures.CarFixture.randomModel
import com.makarytskyi.rentcar.fixtures.CarFixture.randomPlate
import com.makarytskyi.rentcar.fixtures.CarFixture.randomPrice
import com.makarytskyi.rentcar.fixtures.CarFixture.updateDomainCar
import com.makarytskyi.rentcar.fixtures.Utils.defaultSize
import com.makarytskyi.rentcar.fixtures.Utils.firstPage
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import reactor.kotlin.test.test

internal class MongoCarRepositoryTest : ContainerBase {

    @Autowired
    @Qualifier("mongoCarRepository")
    private lateinit var carRepository: CarRepositoryOutputPort

    @Test
    fun `create should insert car and return it with id`() {
        // GIVEN
        val car = createCarRequest()
        val expected = createdCar(car)

        // WHEN
        val result = carRepository.create(car)

        // THEN
        result
            .test()
            .assertNext {
                assertNotNull(it.id, "Car should have non-null id after saving")
                assertEquals(expected.copy(id = it.id), it)
            }
            .verifyComplete()
    }

    @Test
    fun `findAll should find all cars`() {
        // GIVEN
        val request = createCarRequest()
        val insertedCar = carRepository.create(request).block()

        // WHEN
        val cars = carRepository.findAll(firstPage, defaultSize)

        // THEN
        cars.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(insertedCar)
            }
            .verifyComplete()
    }

    @Test
    fun `patch should partially update color of car`() {
        // GIVEN
        val color = DomainCar.CarColor.BLUE
        val createRequest = createCarRequest()
        val car = carRepository.create(createRequest).block()!!
        val updateCar = emptyCarPatch().copy(color = color)
        val request = updateDomainCar(updateCar, car)

        // WHEN
        val updated = carRepository.patch(car.id, request)

        // THEN
        updated
            .test()
            .assertNext {
                assertEquals(car.price, it.price)
                assertEquals(color, it.color)
            }
            .verifyComplete()
    }

    @Test
    fun `patch should partially update price of car`() {
        // GIVEN
        val price = randomPrice()
        val createRequest = createCarRequest()
        val car = carRepository.create(createRequest).block()!!
        val updateCar = emptyCarPatch().copy(price = price)
        val request = updateDomainCar(updateCar, car)

        // WHEN
        val updated = carRepository.patch(car.id, request)

        // THEN
        updated
            .test()
            .assertNext {
                assertEquals(price, it.price)
                assertEquals(car.color, it.color)
            }
            .verifyComplete()
    }

    @Test
    fun `findByPlate should find existing car by plate`() {
        // GIVEN
        val plate = randomPlate()
        val createRequest = createCarRequest().copy(plate = plate)
        carRepository.create(createRequest).block()

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
        val unexistingPlate = randomPlate()

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
        val brand = randomBrand()
        val createRequest1 = createCarRequest().copy(brand = brand)
        val createRequest2 = createCarRequest().copy(brand = brand)
        val car1 = carRepository.create(createRequest1).block()
        val car2 = carRepository.create(createRequest2).block()

        // WHEN
        val foundCars = carRepository.findAllByBrand(brand)

        // THEN
        foundCars.collectList()
            .test()
            .assertNext {
                assertThat(it).containsAll(listOf(car1, car2))
            }
            .verifyComplete()
    }

    @Test
    fun `findAllByBrandAndModel should find cars by brand and model`() {
        // GIVEN
        val brand = randomBrand()
        val model = randomModel()
        val createRequest1 = createCarRequest().copy(brand = brand, model = model)
        val createRequest2 = createCarRequest().copy(brand = brand, model = model)
        val car1 = carRepository.create(createRequest1).block()
        val car2 = carRepository.create(createRequest2).block()

        // WHEN
        val foundCars = carRepository.findAllByBrandAndModel(brand, model)

        // THEN
        foundCars.collectList()
            .test()
            .assertNext {
                assertThat(it).containsAll(listOf(car1, car2))
            }
            .verifyComplete()
    }

    @Test
    fun `findById should return existing car by id`() {
        // GIVEN
        val car = carRepository.create(createCarRequest()).block()!!

        // WHEN
        val foundCar = carRepository.findById(car.id)

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
        val car = carRepository.create(createCarRequest()).block()!!

        // WHEN
        carRepository.deleteById(car.id).block()!!

        // THEN
        carRepository.findById(car.id)
            .test()
            .verifyComplete()
    }
}
