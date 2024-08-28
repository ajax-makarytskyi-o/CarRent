package services

import fixtures.CarFixture.carId
import fixtures.RepairingFixture.createRepairingEntity
import fixtures.RepairingFixture.createRepairingRequest
import fixtures.RepairingFixture.createdRepairing
import fixtures.RepairingFixture.createdRepairingResponse
import fixtures.RepairingFixture.existingCar
import fixtures.RepairingFixture.existingRepairing
import fixtures.RepairingFixture.repairingId
import fixtures.RepairingFixture.responseRepairing
import fixtures.RepairingFixture.updateRepairingEntity
import fixtures.RepairingFixture.updateRepairingRequest
import fixtures.RepairingFixture.updatedRepairing
import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.RepairingRepository
import com.makarytskyi.rentcar.service.RepairingService
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
class RepairingServiceTests {
    @Mock
    lateinit var repairingRepository: RepairingRepository

    @Mock
    lateinit var carRepository: CarRepository

    @InjectMocks
    lateinit var repairingService: RepairingService

    @Test
    fun `getById should return RepairingResponse when Repairing exists`() {
        //GIVEN
        whenever(repairingRepository.findById(repairingId)).thenReturn(existingRepairing)

        //WHEN
        val result = repairingService.getById(repairingId)

        //THEN
        assertEquals(responseRepairing, result)
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
        val repairings = listOf(existingRepairing)
        val expected = listOf(responseRepairing)
        whenever(repairingRepository.findAll()).thenReturn(repairings)

        //WHEN
        val result = repairingService.findAll()

        //THEN
        verify(repairingRepository).findAll()
        assertEquals(expected, result)
    }

    @Test
    fun `findAll should return empty List of RepairingResponse if repository return empty List`() {
        //GIVEN
        whenever(repairingRepository.findAll()).thenReturn(emptyList())

        //WHEN
        val result = repairingService.findAll()

        //THEN
        verify(repairingRepository).findAll()
        Assertions.assertEquals(emptyList<Repairing>(), result)
    }

    @Test
    fun `should create repairing successfully`() {
        //GIVEN
        whenever(repairingRepository.create(createRepairingEntity)).thenReturn(createdRepairing)
        whenever(carRepository.findById(carId)).thenReturn(existingCar)

        //WHEN
        val result = repairingService.create(createRepairingRequest)

        //THEN
        verify(repairingRepository).create(createRepairingEntity)
        assertEquals(createdRepairingResponse, result)
    }

    @Test
    fun `should throw IllegalArgumentException if car doesn't exist`() {
        //GIVEN
        whenever(carRepository.findById(carId)).thenReturn(null)

        //WHEN //THEN
        assertThrows(IllegalArgumentException::class.java, { repairingService.create(createRepairingRequest) })
    }

    @Test
    fun `update should return updated repairing`() {
        //GIVEN
        whenever(repairingRepository.update(repairingId, updateRepairingEntity)).thenReturn(updatedRepairing)

        //WHEN
        val result = repairingService.update(repairingId, updateRepairingRequest)

        //THEN
        assertNotNull(result)
        verify(repairingRepository).update(repairingId, updateRepairingEntity)
    }

    @Test
    fun `update should throw ResourceNotFoundException if repairing is not found`() {
        //GIVEN
        val repairingId = "unknown"

        //WHEN //THEN
        assertThrows(
            ResourceNotFoundException::class.java,
            { repairingService.update(repairingId, updateRepairingRequest) })
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
