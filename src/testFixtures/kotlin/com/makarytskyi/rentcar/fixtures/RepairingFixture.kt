package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.repairing.AggregatedRepairingResponse
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.fixtures.Utils.getDateFromNow
import com.makarytskyi.rentcar.fixtures.Utils.randomPrice
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.MongoRepairing.RepairingStatus
import com.makarytskyi.rentcar.model.patch.MongoRepairingPatch
import com.makarytskyi.rentcar.model.projection.AggregatedMongoRepairing
import org.bson.types.ObjectId

object RepairingFixture {
    var tomorrow = getDateFromNow(1)
    var monthAfter = getDateFromNow(30)

    fun randomRepairing(carId: ObjectId?) = MongoRepairing(
        id = ObjectId(),
        carId = carId,
        date = tomorrow,
        price = randomPrice(),
        status = RepairingStatus.PENDING,
    )

    fun emptyRepairing() = MongoRepairing(
        id = null,
        carId = null,
        date = null,
        price = null,
        status = null,
    )

    fun emptyRepairingPatch() = MongoRepairingPatch(
        price = null,
        status = null,
    )

    fun responseRepairing(mongoRepairing: MongoRepairing) = RepairingResponse(
        id = mongoRepairing.id.toString(),
        carId = mongoRepairing.carId.toString(),
        date = mongoRepairing.date,
        price = mongoRepairing.price,
        status = mongoRepairing.status,
    )

    fun emptyRepairingResponse() = RepairingResponse(
        id = "",
        carId = "",
        date = null,
        price = null,
        status = null,
    )

    fun responseAggregatedRepairing(mongoRepairing: AggregatedMongoRepairing) = AggregatedRepairingResponse(
        id = mongoRepairing.id.toString(),
        car = CarResponse.from(mongoRepairing.car ?: MongoCar()),
        date = mongoRepairing.date,
        price = mongoRepairing.price,
        status = mongoRepairing.status,
    )

    fun createRepairingRequest(mongoCar: MongoCar) = CreateRepairingRequest(
        carId = mongoCar.id.toString(),
        date = monthAfter,
        price = randomPrice(),
        status = RepairingStatus.IN_PROGRESS,
    )

    fun createRepairingEntity(request: CreateRepairingRequest) = MongoRepairing(
        id = null,
        carId = ObjectId(request.carId),
        date = request.date,
        price = request.price,
        status = request.status,
    )

    fun createdRepairing(mongoRepairing: MongoRepairing) = mongoRepairing.copy(id = ObjectId())

    fun updateRepairingRequest() = UpdateRepairingRequest(
        price = randomPrice(),
        status = RepairingStatus.COMPLETED,
    )

    fun repairingPatch(request: UpdateRepairingRequest) = MongoRepairingPatch(
        price = request.price,
        status = request.status,
    )

    fun updatedRepairing(oldMongoRepairing: MongoRepairing, request: UpdateRepairingRequest) =
        oldMongoRepairing.copy(price = request.price, status = request.status)

    fun randomAggregatedRepairing(car: MongoCar?) = AggregatedMongoRepairing(
        id = ObjectId(),
        car = car,
        date = tomorrow,
        price = randomPrice(),
        status = RepairingStatus.PENDING,
    )

    fun emptyAggregatedRepairing() = AggregatedMongoRepairing(
        id = null,
        car = null,
        date = null,
        price = null,
        status = null,
    )

    fun emptyAggregatedRepairingResponse() = AggregatedRepairingResponse(
        id = "",
        car = CarResponse.from(MongoCar()),
        date = null,
        price = null,
        status = null,
    )
}
