package com.makarytskyi.rentcar.repairing.application.service

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.rentcar.car.application.port.output.CarRepositoryOutputPort
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.RepairingFixture.createRepairingRequest
import com.makarytskyi.rentcar.fixtures.RepairingFixture.createdRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.domainRepairingPatch
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomAggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.responseAggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.responseRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.updateDomainRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.updatedRepairing
import com.makarytskyi.rentcar.repairing.application.port.output.CreateRepairingProducerOutputPort
import com.makarytskyi.rentcar.repairing.application.port.output.RepairingRepositoryOutputPort
import com.makarytskyi.rentcar.repairing.infrastructure.rest.mapper.toResponse
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
    lateinit var repairingRepository: RepairingRepositoryOutputPort

    @MockK
    lateinit var carRepository: CarRepositoryOutputPort

    @MockK
    lateinit var kafkaProducer: CreateRepairingProducerOutputPort

    @InjectMockKs
    lateinit var repairingService: RepairingService

    @Test
    fun `getById should return repairing response when repairing exists`() {
        // GIVEN
        val car = randomCar()
        val repairing = randomAggregatedRepairing(car)
        val response = responseAggregatedRepairing(repairing)
        every { repairingRepository.findFullById(repairing.id) } returns repairing.toMono()

        // WHEN
        val result = repairingService.getFullById(repairing.id).map { it.toResponse() }

        // THEN
        result
            .test()
            .expectNext(response)
            .verifyComplete()

        verify { repairingRepository.findFullById(repairing.id) }
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
        val result = repairingService.findFullAll(0, 10).map { it.toResponse() }

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
        val request = createRepairingRequest(car.id)
        val createdRepairing = createdRepairing(request)
        val response = responseRepairing(createdRepairing)
        every { repairingRepository.create(request) } returns createdRepairing.toMono()
        every { carRepository.findById(car.id) } returns car.toMono()
        every { kafkaProducer.sendCreateRepairing(any()) } returns Mono.empty()

        // WHEN
        val result = repairingService.create(request).map { it.toResponse() }

        // THEN
        result
            .test()
            .expectNext(response)
            .verifyComplete()

        verify { repairingRepository.create(request) }
    }

    @Test
    fun `should return NotFoundException if car doesn't exist`() {
        // GIVEN
        val car = randomCar()
        val request = createRepairingRequest(car.id)
        every { carRepository.findById(car.id) } returns Mono.empty()

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
        val request = domainRepairingPatch()
        val domainRequest = updateDomainRepairing(request, repairing)

        val updatedRepairing = updatedRepairing(repairing, request)
        every { repairingRepository.findById(repairing.id) } returns repairing.toMono()
        every { repairingRepository.patch(repairing.id, domainRequest) } returns updatedRepairing.toMono()

        // WHEN
        val result = repairingService.patch(repairing.id, request)

        // THEN
        result
            .test()
            .expectNext(updatedRepairing)
            .verifyComplete()

        verify { repairingRepository.patch(repairing.id, domainRequest) }
    }

    @Test
    fun `patch should return NotFoundException if repairing is not found`() {
        // GIVEN
        val repairingId = "unknown"
        val request = domainRepairingPatch()
        every { repairingRepository.findById(repairingId) } returns Mono.empty()

        // WHEN // THEN
        repairingService.patch(repairingId, request)
            .test()
            .verifyError<NotFoundException>()

        verify { repairingRepository.findById(repairingId) }
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
