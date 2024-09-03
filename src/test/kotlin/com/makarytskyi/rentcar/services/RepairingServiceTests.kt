package com.makarytskyi.rentcar.services

import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.RepairingRepository
import com.makarytskyi.rentcar.service.impl.RepairingServiceImpl
import fixtures.CarFixture.carId
import fixtures.CarFixture.existingCar
import fixtures.RepairingFixture.createRepairingEntity
import fixtures.RepairingFixture.createRepairingRequest
import fixtures.RepairingFixture.createdRepairing
import fixtures.RepairingFixture.existingRepairing
import fixtures.RepairingFixture.repairingId
import fixtures.RepairingFixture.responseRepairing
import fixtures.RepairingFixture.updateRepairingEntity
import fixtures.RepairingFixture.updateRepairingRequest
import fixtures.RepairingFixture.updatedRepairing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class RepairingServiceTests {

    @Mock
    lateinit var repairingRepository: RepairingRepository

    @Mock
    lateinit var carRepository: CarRepository

    @InjectMocks
    lateinit var repairingService: RepairingServiceImpl

    @Test
    fun `getById should return RepairingResponse when Repairing exists`() {
        //GIVEN
        val car = existingCar()
        val repairing = existingRepairing(car)
        val response = responseRepairing(repairing)
        whenever(repairingRepository.findById(repairingId)).thenReturn(repairing)

        //WHEN
        val result = repairingService.getById(repairingId)

        //THEN
        assertEquals(response, result)
        verify(repairingRepository).findById(repairingId)
    }

    @Test
    fun `getById should return throw ResourceNotFoundException`() {
        //GIVEN
        whenever(repairingRepository.findById(repairingId)).thenReturn(null)

        //WHEN //THEN
        assertThrows(ResourceNotFoundException::class.java, { repairingService.getById(repairingId) })
        verify(repairingRepository).findById(repairingId)
    }

    @Test
    fun `findAll should return List of RepairingResponse`() {
        //GIVEN
        val car = existingCar()
        val repairing = existingRepairing(car)
        val response = responseRepairing(repairing)
        val repairings = listOf(repairing)
        val expected = listOf(response)
        whenever(repairingRepository.findAll()).thenReturn(repairings)

        //WHEN
        val result = repairingService.findAll()

        //THEN
        assertEquals(expected, result)
        verify(repairingRepository).findAll()
    }

    @Test
    fun `findAll should return empty List of RepairingResponse if repository return empty List`() {
        //GIVEN
        whenever(repairingRepository.findAll()).thenReturn(emptyList())

        //WHEN
        val result = repairingService.findAll()

        //THEN
        Assertions.assertEquals(emptyList<Repairing>(), result)
        verify(repairingRepository).findAll()
    }

    @Test
    fun `should create repairing successfully`() {
        //GIVEN
        val car = existingCar()
        val request = createRepairingRequest(car)
        val requestEntity = createRepairingEntity(request)
        val createdRepairing = createdRepairing(requestEntity)
        val response = responseRepairing(createdRepairing)
        whenever(repairingRepository.create(requestEntity)).thenReturn(createdRepairing)
        whenever(carRepository.findById(carId)).thenReturn(car)

        //WHEN
        val result = repairingService.create(request)

        //THEN
        assertEquals(response, result)
        verify(repairingRepository).create(requestEntity)
    }

    @Test
    fun `should throw IllegalArgumentException if car doesn't exist`() {
        //GIVEN
        val car = existingCar()
        val request = createRepairingRequest(car)
        whenever(carRepository.findById(carId)).thenReturn(null)

        //WHEN //THEN
        assertThrows(IllegalArgumentException::class.java, { repairingService.create(request) })
    }

    @Test
    fun `update should return updated repairing`() {
        //GIVEN
        val car = existingCar()
        val repairing = existingRepairing(car)
        val request = updateRepairingRequest()
        val requestEntity = updateRepairingEntity(request)
        val updatedRepairing = updatedRepairing(repairing, request)
        whenever(repairingRepository.update(repairingId, requestEntity)).thenReturn(updatedRepairing)

        //WHEN
        val result = repairingService.update(repairingId, request)

        //THEN
        assertNotNull(result)
        verify(repairingRepository).update(repairingId, requestEntity)
    }

    @Test
    fun `update should throw ResourceNotFoundException if repairing is not found`() {
        //GIVEN
        val repairingId = "unknown"

        //WHEN //THEN
        assertThrows(
            ResourceNotFoundException::class.java,
            { repairingService.update(repairingId, updateRepairingRequest()) })
    }

    @Test
    fun `deleteById should be successful`() {
        //GIVEN
        val repairingId = "someId"

        //WHEN //THEN
        assertNotNull(repairingService.deleteById(repairingId))
        verify(repairingRepository).deleteById(repairingId)
    }
}
