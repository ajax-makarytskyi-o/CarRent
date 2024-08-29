package fixtures

import fixtures.CarFixture.carId
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.model.Car
import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.model.Repairing.RepairingStatus
import fixtures.Utils.generateString
import java.util.Calendar
import java.util.Date
import kotlin.random.Random
import org.bson.types.ObjectId

object RepairingFixture {
    val repairingId = ObjectId().toHexString()
    val createdRepairingId = ObjectId().toHexString()
    var tommorow = Calendar.getInstance()
    var monthAfter = Calendar.getInstance()

    init {
        tommorow.add(Calendar.DAY_OF_YEAR, 1)
        monthAfter.add(Calendar.MONTH, 1)
    }

    fun existingRepairing(car: Car) = Repairing(
        id = repairingId,
        carId = car.id,
        date = Date.from(tommorow.toInstant()),
        price = Random.nextInt(300),
        status = RepairingStatus.PENDING,
    )

    fun responseRepairing(repairing: Repairing) = RepairingResponse(
        id = repairing.id ?: repairingId,
        carId = repairing.carId ?: "",
        date = repairing.date,
        price = repairing.price,
        status = repairing.status,
    )

    fun createRepairingRequest(car: Car) = CreateRepairingRequest(
        carId = car.id ?: "",
        date = Date.from(monthAfter.toInstant()),
        price = Random.nextInt(300),
        status = RepairingStatus.IN_PROGRESS,
    )

    fun createRepairingEntity(request: CreateRepairingRequest) = Repairing(
        id = null,
        carId = request.carId,
        date = request.date,
        price = request.price,
        status = request.status,
    )

    fun createdRepairing(repairing: Repairing) = repairing.copy(id = createdRepairingId)

    fun createdRepairingResponse(repairing: Repairing) = RepairingResponse(
        id = repairing.id ?: createdRepairingId,
        carId = repairing.carId ?: "",
        date = repairing.date ?: Date.from(monthAfter.toInstant()),
        price = repairing.price,
        status = repairing.status,
    )

    fun updateRepairingRequest() = UpdateRepairingRequest(
        price = Random.nextInt(300),
        status = RepairingStatus.COMPLETED,
    )

    fun updateRepairingEntity(request: UpdateRepairingRequest) = Repairing(
        id = null,
        carId = null,
        date = null,
        price = request.price,
        status = request.status,
    )

    fun updatedRepairing(oldRepairing: Repairing, request: UpdateRepairingRequest) =
        oldRepairing.copy(price = request.price, status = request.status)
}
