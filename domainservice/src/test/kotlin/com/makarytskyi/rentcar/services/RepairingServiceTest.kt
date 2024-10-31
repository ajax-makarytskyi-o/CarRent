package com.makarytskyi.rentcar.services

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.RepairingFixture.createRepairingEntity
import com.makarytskyi.rentcar.fixtures.RepairingFixture.createRepairingRequest
import com.makarytskyi.rentcar.fixtures.RepairingFixture.createdRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomAggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.repairingPatch
import com.makarytskyi.rentcar.fixtures.RepairingFixture.responseAggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.responseRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.updateRepairingRequest
import com.makarytskyi.rentcar.fixtures.RepairingFixture.updatedRepairing
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.RepairingRepository
import com.makarytskyi.rentcar.service.impl.RepairingServiceImpl
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlin.test.Test
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
internal class RepairingServiceTest {

    @MockK
    lateinit var repairingRepository: RepairingRepository

    @MockK
    lateinit var carRepository: CarRepository

    @InjectMockKs
    lateinit var repairingService: RepairingServiceImpl

    @Test
    fun `getById should return repairing response when repairing exists`() {
        // GIVEN
        val car = randomCar()
        val repairing = randomAggregatedRepairing(car)
        val response = responseAggregatedRepairing(repairing)
        every { repairingRepository.findFullById(repairing.id.toString()) } returns repairing.toMono()

        // WHEN
        val result = repairingService.getFullById(repairing.id.toString())

        // THEN
        result
            .test()
            .expectNext(response)
            .verifyComplete()

        verify { repairingRepository.findFullById(repairing.id.toString()) }
    }

    @Test
    fun `getById should return NotFoundException`() {
        // GIVEN
        val repairingId = ObjectId().toString()
        every { repairingRepository.findFullById(repairingId) } returns Mono.empty()

        // WHEN // THEN
        repairingService.getFullById(repairingId)
            .test()
            .verifyError<NotFoundException>()

        verify { repairingRepository.findFullById(repairingId) }
    }

    @Test
    fun `findAll should return repairing responses`() {
        // GIVEN
        val car = randomCar()
        val repairing = randomAggregatedRepairing(car)
        val repairings = listOf(repairing)
        val response = responseAggregatedRepairing(repairing)
        every { repairingRepository.findFullAll(0, 10) } returns repairings.toFlux()

        // WHEN
        val result = repairingService.findFullAll(0, 10)

        // THEN
        result.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(response)
            }
            .verifyComplete()

        verify { repairingRepository.findFullAll(0, 10) }
    }

    @Test
    fun `findAll should return empty if repository returned empty`() {
        // GIVEN
        every { repairingRepository.findFullAll(0, 10) } returns Flux.empty()

        // WHEN
        val result = repairingService.findFullAll(0, 10)

        // THEN
        result
            .test()
            .verifyComplete()

        verify { repairingRepository.findFullAll(0, 10) }
    }

    @Test
    fun `should create repairing successfully`() {
        // GIVEN
        val car = randomCar()
        val request = createRepairingRequest(car)
        val requestEntity = createRepairingEntity(request)
        val createdRepairing = createdRepairing(requestEntity)
        val response = responseRepairing(createdRepairing)
        every { repairingRepository.create(requestEntity) } returns createdRepairing.toMono()
        every { carRepository.findById(car.id.toString()) } returns car.toMono()

        // WHEN
        val result = repairingService.create(request)

        // THEN
        result
            .test()
            .expectNext(response)
            .verifyComplete()

        verify { repairingRepository.create(requestEntity) }
    }

    @Test
    fun `should return NotFoundException if car doesn't exist`() {
        // GIVEN
        val car = randomCar()
        val request = createRepairingRequest(car)
        every { carRepository.findById(car.id.toString()) } returns Mono.empty()

        // WHEN // THEN
        repairingService.create(request)
            .test()
            .verifyError<NotFoundException>()

        verify { carRepository.findById(request.carId) }
    }

    @Test
    fun `patch should return updated repairing`() {
        // GIVEN
        val car = randomCar()
        val repairing = randomRepairing(car.id)
        val request = updateRepairingRequest()
        val requestEntity = repairingPatch(request)
        val updatedRepairing = updatedRepairing(repairing, request)
        every { repairingRepository.patch(repairing.id.toString(), requestEntity) } returns updatedRepairing.toMono()

        // WHEN
        val result = repairingService.patch(repairing.id.toString(), request)

        // THEN
        result
            .test()
            .expectNext(responseRepairing(updatedRepairing))
            .verifyComplete()

        verify { repairingRepository.patch(repairing.id.toString(), requestEntity) }
    }

    @Test
    fun `patch should return NotFoundException if repairing is not found`() {
        // GIVEN
        val repairingId = "unknown"
        val request = updateRepairingRequest()
        every { repairingRepository.patch(repairingId, repairingPatch(request)) } returns Mono.empty()

        // WHEN // THEN
        repairingService.patch(repairingId, request)
            .test()
            .verifyError<NotFoundException>()

        verify { repairingRepository.patch(repairingId, repairingPatch(request)) }
    }

    @Test
    fun `deleteById should be successful`() {
        // GIVEN
        val repairingId = "someId"
        every { repairingRepository.deleteById(repairingId) } returns Mono.empty()

        // WHEN // THEN
        repairingService.deleteById(repairingId)
            .test()
            .verifyComplete()

        verify { repairingRepository.deleteById(repairingId) }
    }
}
