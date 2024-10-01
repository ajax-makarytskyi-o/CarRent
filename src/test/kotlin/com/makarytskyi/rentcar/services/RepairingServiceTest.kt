package com.makarytskyi.rentcar.services

import com.makarytskyi.rentcar.exception.NotFoundException
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class RepairingServiceTest {

    @Mock
    lateinit var repairingRepository: RepairingRepository

    @Mock
    lateinit var carRepository: CarRepository

    @InjectMocks
    lateinit var repairingService: RepairingServiceImpl

    @Test
    fun `getById should return RepairingResponse when Repairing exists`() {
        // GIVEN
        val car = randomCar()
        val repairing = randomAggregatedRepairing(car)
        val response = responseAggregatedRepairing(repairing)
        whenever(repairingRepository.findById(repairing.id.toString())).thenReturn(repairing)

        // WHEN
        val result = repairingService.getById(repairing.id.toString())

        // THEN
        assertEquals(response, result)
        verify(repairingRepository).findById(repairing.id.toString())
    }

    @Test
    fun `getById should return throw ResourceNotFoundException`() {
        // GIVEN
        val repairingId = ObjectId().toString()
        whenever(repairingRepository.findById(repairingId)).thenReturn(null)

        // WHEN // THEN
        assertThrows(NotFoundException::class.java, { repairingService.getById(repairingId) })
        verify(repairingRepository).findById(repairingId)
    }

    @Test
    fun `findAll should return List of RepairingResponse`() {
        // GIVEN
        val car = randomCar()
        val repairing = randomAggregatedRepairing(car)
        val response = responseAggregatedRepairing(repairing)
        val repairings = listOf(repairing)
        val expected = listOf(response)
        whenever(repairingRepository.findAll(0, 10)).thenReturn(repairings)

        // WHEN
        val result = repairingService.findAll(0, 10)

        // THEN
        assertEquals(expected, result)
        verify(repairingRepository).findAll(0, 10)
    }

    @Test
    fun `findAll should return empty List of RepairingResponse if repository return empty List`() {
        // GIVEN
        whenever(repairingRepository.findAll(0, 10)).thenReturn(emptyList())

        // WHEN
        val result = repairingService.findAll(0, 10)

        // THEN
        assertEquals(emptyList(), result)
        verify(repairingRepository).findAll(0, 10)
    }

    @Test
    fun `should create repairing successfully`() {
        // GIVEN
        val car = randomCar()
        val request = createRepairingRequest(car)
        val requestEntity = createRepairingEntity(request)
        val createdRepairing = createdRepairing(requestEntity)
        val response = responseRepairing(createdRepairing)
        whenever(repairingRepository.create(requestEntity)).thenReturn(createdRepairing)
        whenever(carRepository.findById(car.id.toString())).thenReturn(car)

        // WHEN
        val result = repairingService.create(request)

        // THEN
        assertEquals(response, result)
        verify(repairingRepository).create(requestEntity)
    }

    @Test
    fun `should throw IllegalArgumentException if car doesn't exist`() {
        // GIVEN
        val car = randomCar()
        val request = createRepairingRequest(car)
        whenever(carRepository.findById(car.id.toString())).thenReturn(null)

        // WHEN // THEN
        assertThrows(IllegalArgumentException::class.java, { repairingService.create(request) })
    }

    @Test
    fun `patch should return updated repairing`() {
        // GIVEN
        val car = randomCar()
        val repairing = randomRepairing(car.id)
        val request = updateRepairingRequest()
        val requestEntity = repairingPatch(request)
        val updatedRepairing = updatedRepairing(repairing, request)
        whenever(repairingRepository.patch(repairing.id.toString(), requestEntity)).thenReturn(updatedRepairing)

        // WHEN
        val result = repairingService.patch(repairing.id.toString(), request)

        // THEN
        assertNotNull(result)
        verify(repairingRepository).patch(repairing.id.toString(), requestEntity)
    }

    @Test
    fun `patch should throw ResourceNotFoundException if repairing is not found`() {
        // GIVEN
        val repairingId = "unknown"

        // WHEN // THEN
        assertThrows(
            NotFoundException::class.java,
            { repairingService.patch(repairingId, updateRepairingRequest()) }
        )
    }

    @Test
    fun `deleteById should be successful`() {
        // GIVEN
        val repairingId = "someId"

        // WHEN // THEN
        assertNotNull(repairingService.deleteById(repairingId))
        verify(repairingRepository).deleteById(repairingId)
    }
}
