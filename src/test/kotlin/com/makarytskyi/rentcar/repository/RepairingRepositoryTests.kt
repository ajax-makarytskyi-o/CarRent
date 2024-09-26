package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.MongoRepairing
import fixtures.CarFixture.randomCar
import fixtures.RepairingFixture.randomRepairing
import fixtures.RepairingFixture.unexistingRepairing
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class RepairingRepositoryTests : ContainerBase {

    @Autowired
    lateinit var repairingRepository: RepairingRepository

    @Autowired
    lateinit var carRepository: CarRepository

    @Test
    fun `create should insert repairing and return it with id`() {
        // GIVEN
        val car = carRepository.create(randomCar())

        // WHEN
        val repairing = repairingRepository.create(unexistingRepairing(car.id))

        // THEN
        val foundRepairing = repairingRepository.findById(repairing.id.toString())
        assertNotNull(foundRepairing)
        assertNotNull(repairing.id)
    }

    @Test
    fun `findAll should find all repairings`() {
        // GIVEN
        val car1 = carRepository.create(randomCar())
        val repairing1 = repairingRepository.create(randomRepairing(car1.id))
        val car2 = carRepository.create(randomCar())
        val repairing2 = repairingRepository.create(randomRepairing(car2.id))

        // WHEN
        val allRepairings = repairingRepository.findAll()

        // THEN
        assertTrue(allRepairings.any { it.car?.id == repairing1.carId && it.id == repairing1.id })
        assertTrue(allRepairings.any { it.car?.id == repairing2.carId && it.id == repairing2.id })
    }

    @Test
    fun `update should update price of repairing`() {
        // GIVEN
        val price = 300
        val car = carRepository.create(randomCar())
        val repairing = repairingRepository.create(randomRepairing(car.id))

        val updateRepairing = repairing.copy(
            price = price,
            status = null
        )

        // WHEN
        repairingRepository.update(repairing.id.toString(), updateRepairing)

        // THEN
        val updated = repairingRepository.findById(repairing.id.toString())
        assertEquals(price, updated?.price)
    }

    @Test
    fun `update should update status of repairing`() {
        // GIVEN
        val status = MongoRepairing.RepairingStatus.COMPLETED
        val car = carRepository.create(randomCar())
        val repairing = repairingRepository.create(randomRepairing(car.id))

        val updateRepairing = repairing.copy(
            price = null,
            status = status
        )

        // WHEN
        repairingRepository.update(repairing.id.toString(), updateRepairing)

        // THEN
        val updated = repairingRepository.findById(repairing.id.toString())
        assertEquals(status, updated?.status)
    }

    @Test
    fun `findByStatusAndCarId should return repairings found by status and carId`() {
        // GIVEN
        val status = MongoRepairing.RepairingStatus.COMPLETED
        val car = carRepository.create(randomCar())
        val repairing = randomRepairing(car.id).copy(status = status)
        repairingRepository.create(repairing)

        // WHEN
        val foundRepairings = repairingRepository.findByStatusAndCarId(status, car.id.toString())

        // THEN
        assertTrue(foundRepairings.isNotEmpty())
    }

    @Test
    fun `deleteById should delete repairing by id`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val repairing = repairingRepository.create(randomRepairing(car.id))

        val createdRepairing = repairingRepository.findById(repairing.id.toString())

        // WHEN
        repairingRepository.deleteById(repairing.id.toString())

        // THEN
        val deletedRepairing = repairingRepository.findById(repairing.id.toString())
        assertNotNull(createdRepairing)
        assertNull(deletedRepairing)
    }

    @Test
    fun `findById should return existing repairing by id`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val repairing = randomRepairing(car.id)
        repairingRepository.create(repairing)

        // WHEN
        val foundRepairing = repairingRepository.findById(repairing.id.toString())

        // THEN
        assertNotNull(foundRepairing)
    }

    @Test
    fun `findById should return null if cant find repairing by id`() {
        // GIVEN
        val unexistingId = "unexistingId"

        // WHEN
        val foundRepairing = repairingRepository.findById(unexistingId)

        // THEN
        assertNull(foundRepairing)
    }

    @Test
    fun `findByStatus should return repairings found by status`() {
        // GIVEN
        val status = MongoRepairing.RepairingStatus.IN_PROGRESS
        val car = carRepository.create(randomCar())
        val repairing = randomRepairing(car.id).copy(status = status)
        repairingRepository.create(repairing)

        // WHEN
        val repairings = repairingRepository.findByStatus(status)

        // THEN
        assertTrue(repairings.isNotEmpty())
    }

    @Test
    fun `findByCarId should return repairings found by carId`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val repairing = randomRepairing(car.id)
        repairingRepository.create(repairing)

        // WHEN
        val repairings = repairingRepository.findByCarId(car.id.toString())

        // THEN
        assertTrue(repairings.isNotEmpty())
    }
}
