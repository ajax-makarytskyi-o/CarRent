package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.RepairingFixture.aggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.emptyRepairingPatch
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomRepairing
import com.makarytskyi.rentcar.model.MongoRepairing
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

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
        createdRepairing
            .test()
            .assertNext {
                assertNotNull(it.id, "Repairing should have non-null id after saving")
                assertEquals(repairing.copy(id = it.id), it)
            }
            .verifyComplete()
    }

    @Test
    fun `findAll should find all repairings`() {
        // GIVEN
        val car1 = carRepository.create(randomCar()).block()
        val repairing1 = repairingRepository.create(randomRepairing(car1?.id)).block()
        val fullRepairing1 = aggregatedRepairing(repairing1, car1)

        val car2 = carRepository.create(randomCar()).block()
        val repairing2 = repairingRepository.create(randomRepairing(car2?.id)).block()
        val fullRepairing2 = aggregatedRepairing(repairing2, car2)

        // WHEN
        val allRepairings = repairingRepository.findFullAll(0, 20)

        // THEN
        allRepairings.collectList()
            .test()
            .assertNext { it.containsAll(listOf(fullRepairing1, fullRepairing2)) }
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
        updated
            .test()
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
        foundRepairings.collectList()
            .test()
            .assertNext {
                assertTrue(it.contains(repairing), "Result should contain expected repairing.")
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
        repairingRepository.findFullById(repairing?.id.toString())
            .test()
            .verifyComplete()
    }

    @Test
    fun `findById should return existing repairing by id`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()
        val repairing = repairingRepository.create(randomRepairing(car?.id)).block()
        val fullRepairing = aggregatedRepairing(repairing, car)

        // WHEN
        val foundRepairing = repairingRepository.findFullById(repairing?.id.toString())

        // THEN
        foundRepairing
            .test()
            .expectNext(fullRepairing)
            .verifyComplete()
    }

    @Test
    fun `findById should return empty if cant find repairing by id`() {
        // GIVEN
        val unexistingId = "unexistingId"

        // WHEN
        val foundRepairing = repairingRepository.findFullById(unexistingId)

        // THEN
        foundRepairing
            .test()
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
        repairings.collectList()
            .test()
            .assertNext {
                assertTrue(it.contains(repairing), "Result should contain expected repairing.")
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
        repairings.collectList()
            .test()
            .assertNext {
                assertTrue(it.contains(repairing), "Result should contain expected repairing.")
            }
            .verifyComplete()
    }
}
