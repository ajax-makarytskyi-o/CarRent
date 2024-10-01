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
internal class CarServiceTest {
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
        whenever(carRepository.findAll(0, 10)).thenReturn(mongoCars)

        // WHEN
        val result = carService.findAll(0, 10)

        // THEN
        assertEquals(expected, result)
        verify(carRepository).findAll(0, 10)
    }

    @Test
    fun `findAll should return empty List of CarResponse if repository return empty List`() {
        // GIVEN
        whenever(carRepository.findAll(0, 10)).thenReturn(emptyList())

        // WHEN
        val result = carService.findAll(0, 10)

        // THEN
        assertEquals(emptyList(), result)
        verify(carRepository).findAll(0, 10)
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
    fun `patch should return updated car`() {
        // GIVEN
        val oldCar = randomCar()
        val updateCarRequest = updateCarRequest()
        val updateCarEntity = carPatch(updateCarRequest)
        val updatedCar = updatedCar(oldCar, updateCarRequest)
        whenever(carRepository.patch(oldCar.id.toString(), updateCarEntity)).thenReturn(updatedCar)

        // WHEN
        val result = carService.patch(oldCar.id.toString(), updateCarRequest)

        // THEN
        assertNotNull(result)
        assertEquals(updateCarRequest.price, result.price)
        verify(carRepository).patch(oldCar.id.toString(), updateCarEntity)
    }

    @Test
    fun `patch should throw ResourceNotFoundException if car is not found`() {
        // GIVEN
        val carId = "unknown"
        val updateCarRequest = updateCarRequest()

        // WHEN // THEN
        assertThrows(NotFoundException::class.java, { carService.patch(carId, updateCarRequest) })
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
