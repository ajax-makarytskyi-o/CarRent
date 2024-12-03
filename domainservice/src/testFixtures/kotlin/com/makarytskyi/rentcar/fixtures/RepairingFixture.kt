package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.infrastructure.mongo.entity.MongoCar
import com.makarytskyi.rentcar.car.infrastructure.rest.mapper.toResponse
import com.makarytskyi.rentcar.fixtures.CarFixture.randomPrice
import com.makarytskyi.rentcar.fixtures.Utils.getDateFromNow
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.domain.patch.DomainRepairingPatch
import com.makarytskyi.rentcar.repairing.domain.projection.AggregatedDomainRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.mongo.entity.MongoRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.mongo.entity.projection.AggregatedMongoRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.AggregatedRepairingResponse
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.RepairingResponse
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.UpdateRepairingRequest
import com.makarytskyi.rentcar.repairing.infrastructure.rest.mapper.toResponse
import org.bson.types.ObjectId

object RepairingFixture {
    var tomorrow = getDateFromNow(1)
    var monthAfter = getDateFromNow(30)

    fun randomRepairing(carId: String?) = DomainRepairing(
        id = ObjectId().toString(),
        carId = carId.orEmpty(),
        date = tomorrow,
        price = randomPrice(),
        status = DomainRepairing.RepairingStatus.PENDING,
    )

    fun emptyRepairingPatch() = DomainRepairingPatch(
        price = null,
        status = null,
    )

    fun responseRepairing(repairing: DomainRepairing) = RepairingResponse(
        id = repairing.id.toString(),
        carId = repairing.carId,
        date = repairing.date,
        price = repairing.price,
        status = repairing.status.toResponse(),
    )

    fun responseAggregatedRepairing(repairing: AggregatedDomainRepairing) = AggregatedRepairingResponse(
        id = repairing.id.toString(),
        car = repairing.car.toResponse(),
        date = repairing.date,
        price = repairing.price,
        status = repairing.status.toResponse(),
    )

    fun createRepairingRequest(car: DomainCar) = DomainRepairing(
        carId = car.id.toString(),
        date = monthAfter,
        price = randomPrice(),
        status = DomainRepairing.RepairingStatus.IN_PROGRESS,
    )

    fun createdRepairing(repairing: DomainRepairing) = repairing.copy(id = ObjectId().toString())

    fun updateRepairingRequest() = UpdateRepairingRequest(
        price = randomPrice(),
        status = DomainRepairing.RepairingStatus.COMPLETED,
    )

    fun domainRepairingPatch() = DomainRepairingPatch(
        price = randomPrice(),
        status = DomainRepairing.RepairingStatus.COMPLETED,
    )

    fun updateDomainRepairing(patch: DomainRepairingPatch, oldRepairing: DomainRepairing) = oldRepairing.copy(
        price = patch.price ?: oldRepairing.price,
        status = patch.status ?: oldRepairing.status,
    )

    fun repairingPatch(request: UpdateRepairingRequest) = DomainRepairingPatch(
        price = request.price,
        status = request.status,
    )

    fun updatedRepairing(oldRepairing: DomainRepairing, request: DomainRepairingPatch) =
        oldRepairing.copy(
            price = request.price ?: oldRepairing.price,
            status = request.status ?: oldRepairing.status,
        )

    fun randomAggregatedRepairing(car: DomainCar) = AggregatedDomainRepairing(
        id = ObjectId().toString(),
        car = car,
        date = tomorrow,
        price = randomPrice(),
        status = DomainRepairing.RepairingStatus.PENDING,
    )

    fun aggregatedRepairing(repairing: DomainRepairing, car: DomainCar) = AggregatedDomainRepairing(
        id = repairing.id.toString(),
        car = car,
        date = repairing.date,
        price = repairing.price,
        status = repairing.status,
    )

    fun randomAggregatedMongoRepairing(car: MongoCar?) = AggregatedMongoRepairing(
        id = ObjectId(),
        car = car,
        date = tomorrow,
        price = randomPrice(),
        status = MongoRepairing.RepairingStatus.PENDING,
    )
}
