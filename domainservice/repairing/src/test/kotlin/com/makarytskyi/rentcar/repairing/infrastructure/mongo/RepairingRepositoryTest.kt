package com.makarytskyi.rentcar.repairing.infrastructure.mongo

import com.makarytskyi.rentcar.car.application.port.output.CarOutputPort
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.CarFixture.randomPrice
import com.makarytskyi.rentcar.fixtures.RepairingFixture.aggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.emptyRepairingPatch
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.updateDomainRepairing
import com.makarytskyi.rentcar.repairing.ContainerBase
import com.makarytskyi.rentcar.repairing.application.port.output.RepairingMongoOutputPort
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.mongo.entity.MongoRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.mongo.mapper.toDomain
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

internal class RepairingRepositoryTest : ContainerBase {

    @Autowired
    lateinit var repairingRepository: RepairingMongoOutputPort

    @Autowired
    lateinit var carRepository: CarOutputPort

    @Test
    fun `create should insert repairing and return it with id`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()!!
        val repairing = randomRepairing(car.id).copy(id = null)

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
        val car1 = carRepository.create(randomCar()).block()!!
        val repairing1 = repairingRepository.create(randomRepairing(car1.id)).block()!!
        val fullRepairing1 = aggregatedRepairing(repairing1, car1)

        val car2 = carRepository.create(randomCar()).block()!!
        val repairing2 = repairingRepository.create(randomRepairing(car2.id)).block()!!
        val fullRepairing2 = aggregatedRepairing(repairing2, car2)

        // WHEN
        val allRepairings = repairingRepository.findFullAll(0, 20)

        // THEN
        allRepairings.collectList()
            .test()
            .assertNext {
                assertThat(it).containsAll(listOf(fullRepairing1, fullRepairing2))
            }
            .verifyComplete()
    }

    @Test
    fun `patch should partially update repairing`() {
        // GIVEN
        val price = randomPrice()
        val status = DomainRepairing.RepairingStatus.COMPLETED
        val car = carRepository.create(randomCar()).block()!!
        val repairing = repairingRepository.create(randomRepairing(car.id)).block()!!

        val updateRepairing = emptyRepairingPatch().copy(
            price = price,
            status = status
        )

        val domainUpdate = updateDomainRepairing(updateRepairing, repairing)

        // WHEN
        val updated = repairingRepository.patch(repairing.id.toString(), domainUpdate)

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
        val car = carRepository.create(randomCar()).block()!!
        val repairing = repairingRepository.create(randomRepairing(car.id).copy(status = status.toDomain())).block()

        // WHEN
        val foundRepairings = repairingRepository.findByStatusAndCarId(status.toDomain(), car.id.toString())

        // THEN
        foundRepairings.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(repairing)
            }
            .verifyComplete()
    }

    @Test
    fun `deleteById should delete repairing by id`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()!!
        val repairing = repairingRepository.create(randomRepairing(car.id)).block()!!

        // WHEN
        repairingRepository.deleteById(repairing.id.toString()).block()

        // THEN
        repairingRepository.findFullById(repairing.id.toString())
            .test()
            .verifyComplete()
    }

    @Test
    fun `findById should return existing repairing by id`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()!!
        val repairing = repairingRepository.create(randomRepairing(car.id)).block()!!
        val fullRepairing = aggregatedRepairing(repairing, car)

        // WHEN
        val foundRepairing = repairingRepository.findFullById(repairing.id.toString())

        // THEN
        foundRepairing
            .test()
            .expectNext(fullRepairing)
            .verifyComplete()
    }

    @Test
    fun `findById should return empty if cant find repairing by id`() {
        // GIVEN
        val unexistingId = ObjectId().toString()

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
        val car = carRepository.create(randomCar()).block()!!
        val repairing = repairingRepository.create(randomRepairing(car.id).copy(status = status.toDomain())).block()

        // WHEN
        val repairings = repairingRepository.findByStatus(status.toDomain())

        // THEN
        repairings.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(repairing)
            }
            .verifyComplete()
    }

    @Test
    fun `findByCarId should return repairings found by carId`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()!!
        val repairing = repairingRepository.create(randomRepairing(car.id)).block()

        // WHEN
        val repairings = repairingRepository.findByCarId(car.id.toString())

        // THEN
        repairings.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(repairing)
            }
            .verifyComplete()
    }
}