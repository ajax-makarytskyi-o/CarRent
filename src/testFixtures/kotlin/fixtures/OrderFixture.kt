package fixtures

import com.makarytskyi.rentcar.dto.order.AggregatedOrderResponse
import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoOrder
import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.model.aggregated.AggregatedMongoOrder
import fixtures.CarFixture.responseCar
import fixtures.UserFixture.responseUser
import java.util.Calendar
import java.util.Date
import org.bson.types.ObjectId

object OrderFixture {
    val orderId = ObjectId()
    val createdOrderId = ObjectId()
    var yesterday = Calendar.getInstance()
    var tommorow = Calendar.getInstance()
    var twoDaysAfter = Calendar.getInstance()
    var threeDaysAfter = Calendar.getInstance()
    var monthAfter = Calendar.getInstance()
    var monthAndDayAfter = Calendar.getInstance()

    init {
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        tommorow.add(Calendar.DAY_OF_YEAR, 1)
        twoDaysAfter.add(Calendar.DAY_OF_YEAR, 2)
        threeDaysAfter.add(Calendar.DAY_OF_YEAR, 3)
        monthAfter.add(Calendar.DAY_OF_YEAR, 30)
        monthAndDayAfter.add(Calendar.DAY_OF_YEAR, 31)

    }

    fun unexistingOrder(carId: ObjectId?, userId: ObjectId?) = MongoOrder(
        id = null,
        carId = carId,
        userId = userId,
        from = Date.from(tommorow.toInstant()),
        to = Date.from(twoDaysAfter.toInstant()),
    )


    fun randomOrder(carId: ObjectId?, userId: ObjectId?) = MongoOrder(
        id = ObjectId(),
        carId = carId,
        userId = userId,
        from = Date.from(tommorow.toInstant()),
        to = Date.from(twoDaysAfter.toInstant()),
    )

    fun randomAggregatedOrder(car: MongoCar?, user: MongoUser?) = AggregatedMongoOrder(
        id = ObjectId(),
        car = car,
        user = user,
        from = Date.from(tommorow.toInstant()),
        to = Date.from(twoDaysAfter.toInstant()),
    )

    fun existingAggregatedOrder(mongoCar: MongoCar, mongoUser: MongoUser) = AggregatedMongoOrder(
        id = orderId,
        car = mongoCar,
        user = mongoUser,
        from = Date.from(twoDaysAfter.toInstant()),
        to = Date.from(threeDaysAfter.toInstant()),
    )

    fun existingOrderOnCar(mongoCar: MongoCar, mongoUser: MongoUser) = MongoOrder(
        id = ObjectId(),
        carId = mongoCar.id,
        userId = mongoUser.id,
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
    )

    fun responseOrder(mongoOrder: MongoOrder, mongoCar: MongoCar) = OrderResponse(
        id = mongoOrder.id.toString(),
        carId = mongoOrder.carId.toString(),
        userId = mongoOrder.userId.toString(),
        from = mongoOrder.from,
        to = mongoOrder.to,
        price = mongoCar.price?.toLong(),
    )

    fun responseAggregatedOrder(mongoOrder: AggregatedMongoOrder, mongoCar: MongoCar) = AggregatedOrderResponse(
        id = mongoOrder.id.toString(),
        car = responseCar(mongoOrder.car!!),
        user = responseUser(mongoOrder.user!!),
        from = mongoOrder.from,
        to = mongoOrder.to,
        price = mongoCar.price?.toLong(),
    )

    fun createOrderRequest(mongoCar: MongoCar, mongoUser: MongoUser) = CreateOrderRequest(
        carId = mongoCar.id.toString(),
        userId = mongoUser.id.toString(),
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
    )

    fun createOrderEntity(request: CreateOrderRequest) = MongoOrder(
        id = null,
        carId = ObjectId(request.carId),
        userId = ObjectId(request.userId),
        from = request.from,
        to = request.to,
    )

    fun createdOrder(mongoOrder: MongoOrder) = mongoOrder.copy(id = createdOrderId)

    fun updateOrderRequest() = UpdateOrderRequest(
        from = Date.from(tommorow.toInstant()),
        to = Date.from(twoDaysAfter.toInstant()),
    )

    fun updateOrderEntity(request: UpdateOrderRequest) = MongoOrder(
        id = null,
        carId = null,
        userId = null,
        from = request.from,
        to = request.to,
    )

    fun updatedOrder(oldMongoOrder: AggregatedMongoOrder, request: UpdateOrderRequest) =
        MongoOrder(
            id = oldMongoOrder.id,
            carId = oldMongoOrder.car?.id,
            userId = oldMongoOrder.user?.id,
            from = request.from ?: oldMongoOrder.from,
            to = request.to ?: oldMongoOrder.to,
        )
}
