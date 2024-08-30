package fixtures

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.model.Car
import fixtures.Utils.generateString
import kotlin.random.Random
import org.bson.types.ObjectId

object CarFixture {
    val carId: String = ObjectId().toHexString()
    val newCarPrice = Random.nextInt(500)

    fun existingCar() = Car(
        id = carId,
        brand = generateString(15),
        model = generateString(15),
        price = Random.nextInt(500),
        year = Random.nextInt(1900, 2020),
        plate = generateString(6),
        color = Car.CarColor.BLUE,
    )

    fun responseCar(car: Car) = CarResponse(
        id = carId,
        brand = car.brand ?: generateString(15),
        model = car.model ?: generateString(15),
        price = car.price ?: Random.nextInt(500),
        year = car.year ?: Random.nextInt(1900, 2020),
        plate = car.plate ?: generateString(6),
        color = Car.CarColor.BLUE,
    )

    fun createCarRequest() = CreateCarRequest(
        brand = generateString(15),
        model = generateString(15),
        price = Random.nextInt(500),
        year = Random.nextInt(1900, 2020),
        plate = generateString(6),
        color = Car.CarColor.RED,
    )

    fun createCarEntity(request: CreateCarRequest) = Car(
        id = null,
        brand = request.brand,
        model = request.model,
        price = request.price,
        year = request.year,
        plate = request.plate,
        color = Car.CarColor.RED,
    )

    fun createdCar(car: Car) = car.copy(id = ObjectId().toHexString())

    fun createdCarResponse(car: Car) = CarResponse(
        id = car.id ?: ObjectId().toHexString(),
        brand = car.brand ?: generateString(15),
        model = car.model ?: generateString(15),
        price = car.price ?: Random.nextInt(500),
        year = car.year ?: Random.nextInt(1900, 2020),
        plate = car.plate ?: generateString(6),
        color = Car.CarColor.RED,
    )

    fun updateCarRequest() = UpdateCarRequest(
        price = newCarPrice,
        color = Car.CarColor.GREEN,
    )

    fun updateCarEntity(request: UpdateCarRequest) = Car(
        id = null,
        brand = null,
        model = null,
        price = request.price,
        year = null,
        plate = null,
        color = request.color,
    )

    fun updatedCar(oldCar: Car, request: UpdateCarRequest) = oldCar.copy(price = request.price, color = request.color)
}
