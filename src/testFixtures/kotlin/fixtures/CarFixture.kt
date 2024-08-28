package fixtures

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.model.Car

object CarFixture {
    const val carId: String = "1231441"
    const val createdCarId: String = "83465294"
    const val newCarPrice = 250

    val existingCar = Car(
        id = carId,
        brand = "Toyota",
        model = "Corolla",
        price = 150,
        year = 2020,
        plate = "AA1234AA",
        color = Car.CarColor.BLUE,
    )

    val responseCar = CarResponse(
        id = carId,
        brand = "Toyota",
        model = "Corolla",
        price = 150,
        year = 2020,
        plate = "AA1234AA",
        color = Car.CarColor.BLUE,
    )

    val createCarRequest = CreateCarRequest(
        brand = "BMW",
        model = "M3",
        price = 200,
        year = 2016,
        plate = "AA5678AA",
        color = Car.CarColor.RED,
    )

    val createCarEntity = Car(
        id = null,
        brand = "BMW",
        model = "M3",
        price = 200,
        year = 2016,
        plate = "AA5678AA",
        color = Car.CarColor.RED,
    )

    val createdCar = Car(
        id = createdCarId,
        brand = "BMW",
        model = "M3",
        price = 200,
        year = 2016,
        plate = "AA5678AA",
        color = Car.CarColor.RED,
    )

    val createdCarResponse = CarResponse(
        id = createdCarId,
        brand = "BMW",
        model = "M3",
        price = 200,
        year = 2016,
        plate = "AA5678AA",
        color = Car.CarColor.RED,
    )

    val updateCarRequest = UpdateCarRequest(
        price = newCarPrice,
        color = Car.CarColor.GREEN,
    )

    val updateCarEntity = Car(
        id = null,
        brand = null,
        model = null,
        price = newCarPrice,
        year = null,
        plate = null,
        color = Car.CarColor.GREEN,
    )

    val updatedCar = Car(
        id = carId,
        brand = "Toyota",
        model = "Corolla",
        price = newCarPrice,
        year = 2020,
        plate = "AA1234AA",
        color = Car.CarColor.GREEN,
    )
}
