package fixtures

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.repairing.AggregatedRepairingResponse
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.MongoRepairing.RepairingStatus
import com.makarytskyi.rentcar.model.aggregated.AggregatedMongoRepairing
import java.util.Calendar
import java.util.Date
import kotlin.random.Random
import org.bson.types.ObjectId

object RepairingFixture {
    val repairingId = ObjectId()
    val createdRepairingId = ObjectId()
    var tommorow = Calendar.getInstance()
    var monthAfter = Calendar.getInstance()

    init {
        tommorow.add(Calendar.DAY_OF_YEAR, 1)
        monthAfter.add(Calendar.MONTH, 1)
    }

    fun unexistingRepairing(carId: ObjectId?) = MongoRepairing(
        id = null,
        carId = carId,
        date = Date.from(tommorow.toInstant()),
        price = Random.nextInt(300),
        status = RepairingStatus.PENDING,
    )

    fun existingRepairing(mongoCar: MongoCar) = MongoRepairing(
        id = repairingId,
        carId = mongoCar.id,
        date = Date.from(tommorow.toInstant()),
        price = Random.nextInt(300),
        status = RepairingStatus.PENDING,
    )

    fun existingAggregatedRepairing(mongoCar: MongoCar) = AggregatedMongoRepairing(
        id = repairingId,
        car = mongoCar,
        date = Date.from(tommorow.toInstant()),
        price = Random.nextInt(300),
        status = RepairingStatus.PENDING,
    )

    fun responseRepairing(mongoRepairing: MongoRepairing) = RepairingResponse(
        id = mongoRepairing.id.toString(),
        carId = mongoRepairing.carId.toString(),
        date = mongoRepairing.date,
        price = mongoRepairing.price,
        status = mongoRepairing.status,
    )

    fun responseAggregatedRepairing(mongoRepairing: AggregatedMongoRepairing) = AggregatedRepairingResponse(
        id = mongoRepairing.id.toString(),
        car = mongoRepairing.car?.let { CarResponse.from(it) },
        date = mongoRepairing.date,
        price = mongoRepairing.price,
        status = mongoRepairing.status,
    )

    fun createRepairingRequest(mongoCar: MongoCar) = CreateRepairingRequest(
        carId = mongoCar.id.toString(),
        date = Date.from(monthAfter.toInstant()),
        price = Random.nextInt(300),
        status = RepairingStatus.IN_PROGRESS,
    )

    fun createRepairingEntity(request: CreateRepairingRequest) = MongoRepairing(
        id = null,
        carId = ObjectId(request.carId),
        date = request.date,
        price = request.price,
        status = request.status,
    )

    fun createdRepairing(mongoRepairing: MongoRepairing) = mongoRepairing.copy(id = createdRepairingId)

    fun updateRepairingRequest() = UpdateRepairingRequest(
        price = Random.nextInt(300),
        status = RepairingStatus.COMPLETED,
    )

    fun updateRepairingEntity(request: UpdateRepairingRequest) = MongoRepairing(
        id = null,
        carId = null,
        date = null,
        price = request.price,
        status = request.status,
    )

    fun updatedRepairing(oldMongoRepairing: MongoRepairing, request: UpdateRepairingRequest) =
        oldMongoRepairing.copy(price = request.price, status = request.status)

    fun randomRepairing(carId: ObjectId?) = MongoRepairing(
        id = ObjectId(),
        carId = carId,
        date = Date.from(tommorow.toInstant()),
        price = Random.nextInt(300),
        status = RepairingStatus.PENDING,
    )
}
