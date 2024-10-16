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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.bson.types.ObjectId
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

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
        every { carRepository.findById(car.id.toString()) }.returns(Mono.just(car))

        // WHEN
        val result = carService.getById(car.id.toString())

        // THEN
        StepVerifier.create(result)
            .assertNext {
                assertEquals(responseCar, it)
            }
            .verifyComplete()

        verify { carRepository.findById(car.id.toString()) }
    }

    @Test
    fun `getById should return throw ResourceNotFoundException`() {
        // GIVEN
        val carId = ObjectId()
        every { carRepository.findById(carId.toString()) }.returns(Mono.empty())

        // WHEN // THEN
        StepVerifier.create(carService.getById(carId.toString()))
            .expectError(NotFoundException::class.java)

        verify { carRepository.findById(carId.toString()) }
    }

    @Test
    fun `findAll should return List of CarResponse`() {
        // GIVEN
        val car = randomCar()
        val response = responseCar(car)
        val mongoCars: List<MongoCar> = listOf(car)
        val expected = listOf(response)
        every { carRepository.findAll(0, 10) }.returns(Flux.fromIterable(mongoCars))

        // WHEN
        val result = carService.findAll(0, 10)

        // THEN
        StepVerifier.create(result.collectList())
            .assertNext {
                assertTrue(it.containsAll(expected))
            }
            .verifyComplete()

        verify { carRepository.findAll(0, 10) }
    }

    @Test
    fun `findAll should not return anything if repository didn't return anything`() {
        // GIVEN
        every { carRepository.findAll(0, 10) }.returns(Flux.empty())

        // WHEN
        val result = carService.findAll(0, 10)

        // THEN
        StepVerifier.create(result)
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
        every { carRepository.create(requestEntity) }.returns(Mono.just(createdCar))
        every { carRepository.findByPlate(request.plate) }.returns(Mono.empty())

        // WHEN
        val result = carService.create(request)

        // THEN
        StepVerifier.create(result)
            .assertNext {
                assertEquals(carResponse, it)
            }
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
        every { carRepository.patch(oldCar.id.toString(), updateCarEntity) }.returns(Mono.just(updatedCar))

        // WHEN
        val result = carService.patch(oldCar.id.toString(), updateCarRequest)

        // THEN
        StepVerifier.create(result)
            .assertNext {
                assertNotNull(it)
                assertEquals(updateCarRequest.price, it.price)
            }
            .verifyComplete()

        verify { carRepository.patch(oldCar.id.toString(), updateCarEntity) }
    }

    @Test
    fun `patch should throw ResourceNotFoundException if car is not found`() {
        // GIVEN
        val carId = "unknown"
        val updateCarRequest = updateCarRequest()
        every { carRepository.patch(carId, carPatch(updateCarRequest)) }.returns(Mono.empty())

        // WHEN // THEN
        StepVerifier.create(carService.patch(carId, updateCarRequest))
            .verifyError(NotFoundException::class.java)
    }

    @Test
    fun `deleteById should not throw ResourceNotFoundException if car is not found`() {
        // GIVEN
        val carId = "unknown"
        every { carRepository.deleteById(carId) }.returns(Mono.empty())

        // WHEN // THEN
        StepVerifier.create(carService.deleteById(carId))
            .verifyComplete()

        verify { carRepository.deleteById(carId) }
    }

    @Test
    fun `findByPlate should throw ResourceNotFoundException if such car is not found`() {
        // GIVEN
        val plate = "UNKNOWN"

        // WHEN
        every { carRepository.findByPlate(plate) }.returns(Mono.empty())

        // THEN
        StepVerifier.create(carService.getByPlate(plate))
            .expectError(NotFoundException::class.java)

        verify { carRepository.findByPlate(plate) }
    }
}
