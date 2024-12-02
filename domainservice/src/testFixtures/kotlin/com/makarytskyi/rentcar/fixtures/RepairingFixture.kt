package com.makarytskyi.rentcar.fixtures

import com.google.protobuf.Timestamp
import com.makarytskyi.commonmodels.repairing.Repairing
import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.domain.patch.DomainCarPatch
import com.makarytskyi.rentcar.car.infrastructure.rest.mapper.toResponse
import com.makarytskyi.rentcar.fixtures.CarFixture.randomPrice
import com.makarytskyi.rentcar.fixtures.Utils.getDateFromNow
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.domain.patch.DomainRepairingPatch
import com.makarytskyi.rentcar.repairing.domain.projection.AggregatedDomainRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.AggregatedRepairingResponse
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.RepairingResponse
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.UpdateRepairingRequest
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

    fun emptyProtoRepairing() = Repairing
        .newBuilder().also {
            it.id = ""
            it.carId = ""
            it.price = 0.0
            it.date = Timestamp.getDefaultInstance()
            it.status = Repairing.RepairingStatus.REPAIRING_STATUS_UNSPECIFIED
        }
        .build()

    fun emptyRepairingPatch() = DomainRepairingPatch(
        price = null,
        status = null,
    )

    fun responseRepairing(mongoRepairing: DomainRepairing) = RepairingResponse(
        id = mongoRepairing.id.toString(),
        carId = mongoRepairing.carId,
        date = mongoRepairing.date,
        price = mongoRepairing.price,
        status = mongoRepairing.status!!,
    )

    fun responseAggregatedRepairing(mongoRepairing: AggregatedDomainRepairing) = AggregatedRepairingResponse(
        id = mongoRepairing.id.toString(),
        car = mongoRepairing.car!!.toResponse(),
        date = mongoRepairing.date,
        price = mongoRepairing.price,
        status = mongoRepairing.status!!,
    )

    fun createRepairingRequest(mongoCar: DomainCar) = DomainRepairing(
        carId = mongoCar.id.toString(),
        date = monthAfter,
        price = randomPrice(),
        status = DomainRepairing.RepairingStatus.IN_PROGRESS,
    )

    fun createdRepairing(mongoRepairing: DomainRepairing) = mongoRepairing.copy(id = ObjectId().toString())

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

    fun updatedRepairing(oldMongoRepairing: DomainRepairing, request: DomainRepairingPatch) =
        oldMongoRepairing.copy(
            price = request.price ?: oldMongoRepairing.price,
            status = request.status ?: oldMongoRepairing.status
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
}
