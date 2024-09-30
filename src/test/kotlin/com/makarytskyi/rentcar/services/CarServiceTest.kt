package com.makarytskyi.rentcar.services

import com.makarytskyi.rentcar.exception.NotFoundException
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.service.impl.CarServiceImpl
import fixtures.CarFixture.createCarEntity
import fixtures.CarFixture.createCarRequest
import fixtures.CarFixture.createdCar
import fixtures.CarFixture.randomCar
import fixtures.CarFixture.responseCar
import fixtures.CarFixture.updateCarEntity
import fixtures.CarFixture.updateCarRequest
import fixtures.CarFixture.updatedCar
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
internal class CarServiceTests {
    @Mock
    lateinit var carRepository: CarRepository

    @InjectMocks
    lateinit var carService: CarServiceImpl

    @Test
    fun `getById should return CarResponse when Car exists`() {
        // GIVEN
        val car = randomCar()
        val responseCar = responseCar(car)
        whenever(carRepository.findById(car.id.toString())).thenReturn(car)

        // WHEN
        val result = carService.getById(car.id.toString())

        // THEN
        assertEquals(responseCar, result)
        verify(carRepository).findById(car.id.toString())
    }

    @Test
    fun `getById should return throw ResourceNotFoundException`() {
        // GIVEN
        val carId = ObjectId()
        whenever(carRepository.findById(carId.toString())).thenReturn(null)

        // WHEN // THEN
        assertThrows(NotFoundException::class.java, { carService.getById(carId.toString()) })
        verify(carRepository).findById(carId.toString())
    }

    @Test
    fun `findAll should return List of CarResponse`() {
        // GIVEN
        val car = randomCar()
        val response = responseCar(car)
        val mongoCars: List<MongoCar> = listOf(car)
        val expected = listOf(response)
        whenever(carRepository.findAll()).thenReturn(mongoCars)

        // WHEN
        val result = carService.findAll()

        // THEN
        assertEquals(expected, result)
        verify(carRepository).findAll()
    }

    @Test
    fun `findAll should return empty List of CarResponse if repository return empty List`() {
        // GIVEN
        whenever(carRepository.findAll()).thenReturn(emptyList())

        // WHEN
        val result = carService.findAll()

        // THEN
        assertEquals(emptyList(), result)
        verify(carRepository).findAll()
    }

    @Test
    fun `should create car successfully`() {
        // GIVEN
        val request = createCarRequest()
        val requestEntity = createCarEntity(request)
        val createdCar = createdCar(requestEntity)
        val carResponse = responseCar(createdCar)
        whenever(carRepository.create(requestEntity)).thenReturn(createdCar)

        // WHEN
        val result = carService.create(request)

        // THEN
        assertEquals(carResponse, result)
        verify(carRepository).create(requestEntity)
    }

    @Test
    fun `update should return updated car`() {
        // GIVEN
        val oldCar = randomCar()
        val updateCarRequest = updateCarRequest()
        val updateCarEntity = updateCarEntity(updateCarRequest)
        val updatedCar = updatedCar(oldCar, updateCarRequest)
        whenever(carRepository.update(oldCar.id.toString(), updateCarEntity)).thenReturn(updatedCar)

        // WHEN
        val result = carService.update(oldCar.id.toString(), updateCarRequest)

        // THEN
        assertNotNull(result)
        assertEquals(updateCarRequest.price, result.price)
        verify(carRepository).update(oldCar.id.toString(), updateCarEntity)
    }

    @Test
    fun `update should throw ResourceNotFoundException if car is not found`() {
        // GIVEN
        val carId = "unknown"
        val updateCarRequest = updateCarRequest()

        // WHEN // THEN
        assertThrows(NotFoundException::class.java, { carService.update(carId, updateCarRequest) })
    }

    @Test
    fun `deleteById should not throw ResourceNotFoundException if car is not found`() {
        // GIVEN
        val carId = "unknown"

        // WHEN // THEN
        assertNotNull(carService.deleteById(carId))
        verify(carRepository).deleteById(carId)
    }

    @Test
    fun `findByPlate should throw ResourceNotFoundException if such car is not found`() {
        // GIVEN
        val plate = "UNKNOWN"

        // WHEN
        whenever(carRepository.findByPlate(plate)).thenReturn(null)

        // THEN
        assertThrows(NotFoundException::class.java, { carService.getByPlate(plate) })
        verify(carRepository).findByPlate(plate)
    }
}
