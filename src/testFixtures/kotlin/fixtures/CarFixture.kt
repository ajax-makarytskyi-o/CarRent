package fixtures

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.model.MongoCar
import fixtures.Utils.generateString
import kotlin.random.Random
import org.bson.types.ObjectId

object CarFixture {
    val carId = ObjectId()
    val newCarPrice = Random.nextInt(500)
    val model = generateString(15)

    fun randomCar() = MongoCar(
        id = ObjectId(),
        brand = generateString(15),
        model = generateString(15),
        price = Random.nextInt(500),
        year = Random.nextInt(1900, 2020),
        plate = generateString(6),
        color = MongoCar.CarColor.BLUE
    )

    fun unexistingCar() = MongoCar(
        id = null,
        brand = generateString(15),
        model = generateString(15),
        price = Random.nextInt(500),
        year = Random.nextInt(1900, 2020),
        plate = generateString(6),
        color = MongoCar.CarColor.BLUE,
    )

    fun existingCar() = MongoCar(
        id = carId,
        brand = generateString(15),
        model = generateString(15),
        price = Random.nextInt(500),
        year = Random.nextInt(1900, 2020),
        plate = generateString(6),
        color = MongoCar.CarColor.BLUE,
    )

    fun responseCar(mongoCar: MongoCar) = CarResponse(
        id = carId.toString(),
        brand = mongoCar.brand ?: generateString(15),
        model = mongoCar.model ?: generateString(15),
        price = mongoCar.price ?: Random.nextInt(500),
        year = mongoCar.year ?: Random.nextInt(1900, 2020),
        plate = mongoCar.plate ?: generateString(6),
        color = MongoCar.CarColor.BLUE,
    )

    fun createCarRequest() = CreateCarRequest(
        brand = generateString(15),
        model = generateString(15),
        price = Random.nextInt(500),
        year = Random.nextInt(1900, 2020),
        plate = generateString(6),
        color = MongoCar.CarColor.RED,
    )

    fun createCarEntity(request: CreateCarRequest) = MongoCar(
        id = null,
        brand = request.brand,
        model = request.model,
        price = request.price,
        year = request.year,
        plate = request.plate,
        color = MongoCar.CarColor.RED,
    )

    fun createdCar(mongoCar: MongoCar) = mongoCar.copy(id = ObjectId())

    fun createdCarResponse(mongoCar: MongoCar) = CarResponse(
        id = mongoCar.id.toString(),
        brand = mongoCar.brand ?: generateString(15),
        model = mongoCar.model ?: generateString(15),
        price = mongoCar.price ?: Random.nextInt(500),
        year = mongoCar.year ?: Random.nextInt(1900, 2020),
        plate = mongoCar.plate ?: generateString(6),
        color = MongoCar.CarColor.RED,
    )

    fun updateCarRequest() = UpdateCarRequest(
        price = newCarPrice,
        color = MongoCar.CarColor.GREEN,
    )

    fun updateCarEntity(request: UpdateCarRequest) = MongoCar(
        id = null,
        brand = null,
        model = null,
        price = request.price,
        year = null,
        plate = null,
        color = request.color,
    )

    fun updatedCar(oldMongoCar: MongoCar, request: UpdateCarRequest) =
        oldMongoCar.copy(price = request.price, color = request.color)
}
