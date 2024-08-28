package services

import fixtures.CarFixture.carId
import fixtures.CarFixture.createCarEntity
import fixtures.CarFixture.createCarRequest
import fixtures.CarFixture.createdCar
import fixtures.CarFixture.createdCarResponse
import fixtures.CarFixture.existingCar
import fixtures.CarFixture.newCarPrice
import fixtures.CarFixture.responseCar
import fixtures.CarFixture.updateCarEntity
import fixtures.CarFixture.updateCarRequest
import fixtures.CarFixture.updatedCar
import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.model.Car
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.service.CarService

@ExtendWith(MockitoExtension::class)
class CarServiceTests {
    @Mock
    lateinit var carRepository: CarRepository

    @InjectMocks
    lateinit var carService: CarService

    @Test
    fun `getById should return CarResponse when Car exists`() {
        //GIVEN
        whenever(carRepository.findById(carId)).thenReturn(existingCar)

        //WHEN
        val result = carService.getById(carId)

        //THEN
        assertEquals(responseCar, result)
        verify(carRepository).findById(carId)
    }

    @Test
    fun `getById should return throw ResourceNotFoundException`() {
        //GIVEN
        whenever(carRepository.findById(carId)).thenReturn(null)

        //WHEN //THEN
        assertThrows(ResourceNotFoundException::class.java, { carService.getById(carId) })
        verify(carRepository).findById(carId)
    }

    @Test
    fun `findAll should return List of CarResponse`() {
        //GIVEN
        val cars: List<Car> = listOf(existingCar)
        val expected = listOf(responseCar)
        whenever(carRepository.findAll()).thenReturn(cars)

        //WHEN
        val result = carService.findAll()

        //THEN
        verify(carRepository).findAll()
        assertEquals(expected, result)
    }

    @Test
    fun `findAll should return empty List of CarResponse if repository return empty List`() {
        //GIVEN
        whenever(carRepository.findAll()).thenReturn(emptyList())

        //WHEN
        val result = carService.findAll()

        //THEN
        verify(carRepository).findAll()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `should create car successfully`() {
        //GIVEN
        whenever(carRepository.create(createCarEntity)).thenReturn(createdCar)

        //WHEN
        val result = carService.create(createCarRequest)

        //THEN
        verify(carRepository).create(createCarEntity)
        assertEquals(createdCarResponse, result)
    }

    @Test
    fun `update should return updated car`() {
        //GIVEN
        whenever(carRepository.update(carId, updateCarEntity)).thenReturn(updatedCar)

        //WHEN
        val result = carService.update(carId, updateCarRequest)

        //THEN
        assertNotNull(result)
        assertEquals(newCarPrice, result.price)
        verify(carRepository).update(carId, updateCarEntity)
    }

    @Test
    fun `update should throw ResourceNotFoundException if car is not found`() {
        //GIVEN
        val carId = "unknown"

        //WHEN //THEN
        assertThrows(ResourceNotFoundException::class.java, { carService.update(carId, updateCarRequest) })
    }

    @Test
    fun `deleteById should not throw ResourceNotFoundException if car is not found`() {
        //GIVEN
        val carId = "unknown"

        //WHEN //THEN
        assertNotNull(carService.deleteById(carId))
        verify(carRepository).deleteById(carId)
    }

    @Test
    fun `findByPlate should throw ResourceNotFoundException if such car is not found`() {
        //GIVEN
        val plate = "UNKNOWN"

        //WHEN
        whenever(carRepository.findByPlate(plate)).thenReturn(null)

        //THEN
        assertThrows(ResourceNotFoundException::class.java, { carService.getByPlate(plate) })
        verify(carRepository).findByPlate(plate)
    }
}
