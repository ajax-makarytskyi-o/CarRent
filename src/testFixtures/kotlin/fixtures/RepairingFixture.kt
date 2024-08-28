package repairing

import car.CarFixture.carId
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.model.Car
import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.model.Repairing.RepairingStatus
import java.util.Calendar
import java.util.Date

object RepairingFixture {
    const val repairingId = "5254225"
    const val createdRepairingId = "9720582"
    var tommorow = Calendar.getInstance()
    var monthAfter = Calendar.getInstance()

    init {
        tommorow.add(Calendar.DAY_OF_YEAR, 1)
        monthAfter.add(Calendar.MONTH, 1)
    }

    val existingCar = Car(
        id = carId,
        brand = "Toyota",
        model = "Corolla",
        price = 150,
        year = 2020,
        plate = "AA1234AA",
        color = Car.CarColor.BLUE,
    )

    val existingRepairing = Repairing(
        id = repairingId,
        carId = carId,
        date = Date.from(tommorow.toInstant()),
        price = 120,
        status = RepairingStatus.PENDING,
    )

    val responseRepairing = RepairingResponse(
        id = repairingId,
        carId = carId,
        date = Date.from(tommorow.toInstant()),
        price = 120,
        status = RepairingStatus.PENDING,
    )

    val createRepairingRequest = CreateRepairingRequest(
        carId = carId,
        date = Date.from(monthAfter.toInstant()),
        price = 200,
        status = RepairingStatus.IN_PROGRESS,
    )

    val createRepairingEntity = Repairing(
        id = null,
        carId = carId,
        date = Date.from(monthAfter.toInstant()),
        price = 200,
        status = RepairingStatus.IN_PROGRESS,
    )

    val createdRepairing = Repairing(
        id = createdRepairingId,
        carId = carId,
        date = Date.from(monthAfter.toInstant()),
        price = 200,
        status = RepairingStatus.IN_PROGRESS,
    )

    val createdRepairingResponse = RepairingResponse(
        id = createdRepairingId,
        carId = carId,
        date = Date.from(monthAfter.toInstant()),
        price = 200,
        status = RepairingStatus.IN_PROGRESS,
    )

    val updateRepairingRequest = UpdateRepairingRequest(
        price = 250,
        status = RepairingStatus.COMPLETED,
    )

    val updateRepairingEntity = Repairing(
        id = null,
        carId = null,
        date = null,
        price = 250,
        status = RepairingStatus.COMPLETED,
    )

    val updatedRepairing = Repairing(
        id = repairingId,
        carId = carId,
        date = Date.from(tommorow.toInstant()),
        price = 250,
        status = RepairingStatus.COMPLETED,
    )
}
