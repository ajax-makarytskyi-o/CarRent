package com.makarytskyi.rentcar.car.application.service

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.rentcar.car.application.port.output.CarRepositoryOutputPort
import com.makarytskyi.rentcar.car.infrastructure.rest.mapper.toResponse
import com.makarytskyi.rentcar.fixtures.CarFixture.createdCar
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCreateCarRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.responseCar
import com.makarytskyi.rentcar.fixtures.CarFixture.updateCarRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.updateDomainCar
import com.makarytskyi.rentcar.fixtures.CarFixture.updatedCar
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

@ExtendWith(MockKExtension::class)
internal class CarServiceTest {
    @MockK
    lateinit var carRepository: CarRepositoryOutputPort

    @InjectMockKs
    lateinit var carService: CarService

    @Test
    fun `getById should return CarResponse when Car exists`() {
        // GIVEN
        val car = randomCar()
        val responseCar = responseCar(car)
        every { carRepository.findById(car.id) } returns car.toMono()

        // WHEN
        val result = carService.getById(car.id).map { it.toResponse() }

        // THEN
        result
            .test()
            .expectNext(responseCar)
            .verifyComplete()

        verify { carRepository.findById(car.id) }
    }

    @Test
    fun `getById should return NotFoundException`() {
        // GIVEN
        val carId = ObjectId()
        every { carRepository.findById(carId.toString()) } returns Mono.empty()

        // WHEN // THEN
        carService.getById(carId.toString())
            .test()
            .verifyError<NotFoundException>()

        verify { carRepository.findById(carId.toString()) }
    }

    @Test
    fun `findAll should return List of CarResponse`() {
        // GIVEN
        val car = randomCar()
        val response = responseCar(car)
        val mongoCars = listOf(car)
        every { carRepository.findAll(0, 10) } returns mongoCars.toFlux()

        // WHEN
        val result = carService.findAll(0, 10).map { it.toResponse() }

        // THEN
        result.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(response)
            }
            .verifyComplete()

        verify { carRepository.findAll(0, 10) }
    }

    @Test
    fun `findAll should return empty if repository returned empty`() {
        // GIVEN
        every { carRepository.findAll(0, 10) } returns Flux.empty()

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
        val request = randomCreateCarRequest()
        val createdCar = createdCar(request)
        every { carRepository.create(request) } returns createdCar.toMono()

        // WHEN
        val result = carService.create(request)

        // THEN
        result
            .test()
            .assertNext {
                assertEquals(createdCar.copy(id = it.id), it)
            }
            .verifyComplete()

        verify { carRepository.create(request) }
    }

    @Test
    fun `patch should return updated car`() {
        // GIVEN
        val oldCar = randomCar()
        val updateCarRequest = updateCarRequest()
        val patch = updateDomainCar(updateCarRequest, oldCar)
        val updatedCar = updatedCar(oldCar, updateCarRequest)
        every { carRepository.findById(oldCar.id) } returns oldCar.toMono()
        every { carRepository.patch(oldCar.id, patch) } returns updatedCar.toMono()

        // WHEN
        val result = carService.patch(oldCar.id, updateCarRequest)

        // THEN
        result
            .test()
            .assertNext {
                assertEquals(updateCarRequest.price, it.price)
            }
            .verifyComplete()

        verify { carRepository.patch(oldCar.id, patch) }
    }

    @Test
    fun `patch should return NotFoundException if car is not found`() {
        // GIVEN
        val carId = "unknown"
        val request = updateCarRequest()
        every { carRepository.findById(carId) } returns Mono.empty()

        // WHEN // THEN
        carService.patch(carId, request)
            .test()
            .verifyError<NotFoundException>()

        verify { carRepository.findById(carId) }
    }

    @Test
    fun `deleteById should not return NotFoundException if car is not found`() {
        // GIVEN
        val carId = "unknown"
        every { carRepository.deleteById(carId) } returns Mono.empty()

        // WHEN // THEN
        carService.deleteById(carId)
            .test()
            .verifyComplete()

        verify { carRepository.deleteById(carId) }
    }

    @Test
    fun `findByPlate should retun NotFoundException if such car is not found`() {
        // GIVEN
        val plate = "UNKNOWN"
        every { carRepository.findByPlate(plate) } returns Mono.empty()

        // WHEN // THEN
        carService.getByPlate(plate)
            .test()
            .verifyError<NotFoundException>()

        verify { carRepository.findByPlate(plate) }
    }
}
