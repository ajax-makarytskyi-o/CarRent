package com.makarytskyi.rentcar.services

import com.makarytskyi.rentcar.exception.NotFoundException
import com.makarytskyi.rentcar.fixtures.CarFixture.carPatch
import com.makarytskyi.rentcar.fixtures.CarFixture.createCarEntity
import com.makarytskyi.rentcar.fixtures.CarFixture.createCarRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.createdCar
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.CarFixture.responseCar
import com.makarytskyi.rentcar.fixtures.CarFixture.updateCarRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.updatedCar
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.service.impl.CarServiceImpl
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.bson.types.ObjectId
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
internal class CarServiceTest {
    @MockK
    lateinit var carRepository: CarRepository

    @InjectMockKs
    lateinit var carService: CarServiceImpl

    @Test
    fun `getById should return CarResponse when Car exists`() {
        // GIVEN
        val car = randomCar()
        val responseCar = responseCar(car)
        every { carRepository.findById(car.id.toString()) }.returns(car.toMono())

        // WHEN
        val result = carService.getById(car.id.toString())

        // THEN
        result
            .test()
            .expectNext(responseCar)
            .verifyComplete()

        verify { carRepository.findById(car.id.toString()) }
    }

    @Test
    fun `getById should return throw NotFoundException`() {
        // GIVEN
        val carId = ObjectId()
        every { carRepository.findById(carId.toString()) }.returns(Mono.empty())

        // WHEN // THEN
        carService.getById(carId.toString())
            .test()
            .verifyError(NotFoundException::class.java)

        verify { carRepository.findById(carId.toString()) }
    }

    @Test
    fun `findAll should return List of CarResponse`() {
        // GIVEN
        val car = randomCar()
        val response = responseCar(car)
        val mongoCars: List<MongoCar> = listOf(car)
        every { carRepository.findAll(0, 10) }.returns(Flux.fromIterable(mongoCars))

        // WHEN
        val result = carService.findAll(0, 10)

        // THEN
        result.collectList()
            .test()
            .assertNext {
                assertTrue(it.contains(response), "Result should contain expected car response.")
            }
            .verifyComplete()

        verify { carRepository.findAll(0, 10) }
    }

    @Test
    fun `findAll should return empty if repository returned empty`() {
        // GIVEN
        every { carRepository.findAll(0, 10) }.returns(Flux.empty())

        // WHEN
        val result = carService.findAll(0, 10)

        // THEN
        result
            .test()
            .verifyComplete()

        verify { carRepository.findAll(0, 10) }
    }

    @Test
    fun `should create car successfully`() {
        // GIVEN
        val request = createCarRequest()
        val requestEntity = createCarEntity(request)
        val createdCar = createdCar(requestEntity)
        val carResponse = responseCar(createdCar)
        every { carRepository.create(requestEntity) }.returns(createdCar.toMono())

        // WHEN
        val result = carService.create(request)

        // THEN
        result
            .test()
            .expectNext(carResponse)
            .verifyComplete()

        verify { carRepository.create(requestEntity) }
    }

    @Test
    fun `patch should return updated car`() {
        // GIVEN
        val oldCar = randomCar()
        val updateCarRequest = updateCarRequest()
        val updateCarEntity = carPatch(updateCarRequest)
        val updatedCar = updatedCar(oldCar, updateCarRequest)
        every { carRepository.patch(oldCar.id.toString(), updateCarEntity) }.returns(updatedCar.toMono())

        // WHEN
        val result = carService.patch(oldCar.id.toString(), updateCarRequest)

        // THEN
        result
            .test()
            .assertNext {
                assertEquals(updateCarRequest.price, it.price)
            }
            .verifyComplete()

        verify { carRepository.patch(oldCar.id.toString(), updateCarEntity) }
    }

    @Test
    fun `patch should throw NotFoundException if car is not found`() {
        // GIVEN
        val carId = "unknown"
        val updateCarRequest = updateCarRequest()
        every { carRepository.patch(carId, carPatch(updateCarRequest)) }.returns(Mono.empty())

        // WHEN // THEN
        carService.patch(carId, updateCarRequest)
            .test()
            .verifyError(NotFoundException::class.java)

        verify { carRepository.patch(carId, carPatch(updateCarRequest)) }
    }

    @Test
    fun `deleteById should not throw NotFoundException if car is not found`() {
        // GIVEN
        val carId = "unknown"
        every { carRepository.deleteById(carId) }.returns(Mono.empty())

        // WHEN // THEN
        carService.deleteById(carId)
            .test()
            .verifyComplete()

        verify { carRepository.deleteById(carId) }
    }

    @Test
    fun `findByPlate should throw NotFoundException if such car is not found`() {
        // GIVEN
        val plate = "UNKNOWN"
        every { carRepository.findByPlate(plate) }.returns(Mono.empty())

        // WHEN // THEN
        carService.getByPlate(plate)
            .test()
            .verifyError(NotFoundException::class.java)

        verify { carRepository.findByPlate(plate) }
    }
}
