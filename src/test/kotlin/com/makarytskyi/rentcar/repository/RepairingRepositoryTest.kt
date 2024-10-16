package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.RepairingFixture.emptyRepairingPatch
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomRepairing
import com.makarytskyi.rentcar.model.MongoRepairing
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

internal class RepairingRepositoryTest : ContainerBase {

    @Autowired
    lateinit var repairingRepository: RepairingRepository

    @Autowired
    lateinit var carRepository: CarRepository

    @Test
    fun `create should insert repairing and return it with id`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()
        val repairing = randomRepairing(car?.id).copy(id = null)

        // WHEN
        val createdRepairing = repairingRepository.create(repairing)

        // THEN
        StepVerifier.create(createdRepairing)
            .assertNext {
                assertNotNull(it.id)
                assertEquals(repairing.copy(id = it.id), it)
            }
            .verifyComplete()
    }

    @Test
    fun `findAll should find all repairings`() {
        // GIVEN
        val car1 = carRepository.create(randomCar()).block()
        val repairing1 = repairingRepository.create(randomRepairing(car1?.id)).block()
        val car2 = carRepository.create(randomCar()).block()
        val repairing2 = repairingRepository.create(randomRepairing(car2?.id)).block()

        // WHEN
        val allRepairings = repairingRepository.findFullAll(0, 20)

        // THEN
        StepVerifier.create(allRepairings.collectList())
            .assertNext { repairings ->
                assertTrue(repairings.any { it.car?.id == repairing1?.carId && it.id == repairing1?.id })
                assertTrue(repairings.any { it.car?.id == repairing2?.carId && it.id == repairing2?.id })
            }
            .verifyComplete()
    }

    @Test
    fun `patch should partially update repairing`() {
        // GIVEN
        val price = BigDecimal("300")
        val status = MongoRepairing.RepairingStatus.COMPLETED
        val car = carRepository.create(randomCar()).block()
        val repairing = repairingRepository.create(randomRepairing(car?.id)).block()

        val updateRepairing = emptyRepairingPatch().copy(
            price = price,
            status = status
        )

        // WHEN
        val updated = repairingRepository.patch(repairing?.id.toString(), updateRepairing)

        // THEN
        StepVerifier.create(updated)
            .assertNext {
                assertEquals(price, it.price)
                assertEquals(status, it.status)
            }
            .verifyComplete()
    }

    @Test
    fun `findByStatusAndCarId should return repairings found by status and carId`() {
        // GIVEN
        val status = MongoRepairing.RepairingStatus.COMPLETED
        val car = carRepository.create(randomCar()).block()
        val repairing = repairingRepository.create(randomRepairing(car?.id).copy(status = status)).block()

        // WHEN
        val foundRepairings = repairingRepository.findByStatusAndCarId(status, car?.id.toString())

        // THEN
        StepVerifier.create(foundRepairings.collectList())
            .assertNext {
                assertTrue(it.contains(repairing))
            }
            .verifyComplete()
    }

    @Test
    fun `deleteById should delete repairing by id`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()
        val repairing = repairingRepository.create(randomRepairing(car?.id)).block()

        // WHEN
        repairingRepository.deleteById(repairing?.id.toString()).block()

        // THEN
        StepVerifier.create(repairingRepository.findFullById(repairing?.id.toString()))
            .verifyComplete()
    }

    @Test
    fun `findById should return existing repairing by id`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()
        val repairing = repairingRepository.create(randomRepairing(car?.id)).block()

        // WHEN
        val foundRepairing = repairingRepository.findFullById(repairing?.id.toString())

        // THEN
        StepVerifier.create(foundRepairing)
            .assertNext {
                assertEquals(repairing?.id, it.id)
                assertEquals(repairing?.carId, it.car?.id)
                assertEquals(repairing?.date, it.date)
            }
            .verifyComplete()
    }

    @Test
    fun `findById should not return anything if cant find repairing by id`() {
        // GIVEN
        val unexistingId = "unexistingId"

        // WHEN
        val foundRepairing = repairingRepository.findFullById(unexistingId)

        // THEN
        StepVerifier.create(foundRepairing)
            .verifyComplete()
    }

    @Test
    fun `findByStatus should return repairings found by status`() {
        // GIVEN
        val status = MongoRepairing.RepairingStatus.IN_PROGRESS
        val car = carRepository.create(randomCar()).block()
        val repairing = repairingRepository.create(randomRepairing(car?.id).copy(status = status)).block()

        // WHEN
        val repairings = repairingRepository.findByStatus(status)

        // THEN
        StepVerifier.create(repairings.collectList())
            .assertNext {
                assertTrue(it.contains(repairing))
            }
            .verifyComplete()
    }

    @Test
    fun `findByCarId should return repairings found by carId`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()
        val repairing = repairingRepository.create(randomRepairing(car?.id)).block()

        // WHEN
        val repairings = repairingRepository.findByCarId(car?.id.toString())

        // THEN
        StepVerifier.create(repairings.collectList())
            .assertNext {
                assertTrue(it.contains(repairing))
            }
            .verifyComplete()
    }
}
