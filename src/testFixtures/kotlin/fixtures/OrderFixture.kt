package fixtures

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.order.AggregatedOrderResponse
import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoOrder
import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.model.patch.MongoOrderPatch
import com.makarytskyi.rentcar.model.projection.AggregatedMongoOrder
import fixtures.CarFixture.responseCar
import fixtures.UserFixture.responseUser
import fixtures.Utils.getDateFromNow
import java.math.BigDecimal
import org.bson.types.ObjectId

object OrderFixture {
    var yesterday = getDateFromNow(-1)
    var tomorrow = getDateFromNow(1)
    var twoDaysAfter = getDateFromNow(2)
    var monthAfter = getDateFromNow(30)
    var monthAndDayAfter = getDateFromNow(31)

    fun randomOrder(carId: ObjectId?, userId: ObjectId?) = MongoOrder(
        id = ObjectId(),
        carId = carId,
        userId = userId,
        from = tomorrow,
        to = twoDaysAfter,
    )

    fun emptyOrder() = MongoOrder(
        id = null,
        carId = null,
        userId = null,
        from = null,
        to = null,
    )

    fun emptyOrderPatch() = MongoOrderPatch(
        from = null,
        to = null,
    )

    fun randomAggregatedOrder(car: MongoCar?, user: MongoUser?) = AggregatedMongoOrder(
        id = ObjectId(),
        car = car,
        user = user,
        from = tomorrow,
        to = twoDaysAfter,
    )

    fun emptyAggregatedOrder() = AggregatedMongoOrder(
        id = null,
        car = null,
        user = null,
        from = null,
        to = null,
    )

    fun responseOrder(mongoOrder: MongoOrder, mongoCar: MongoCar) = OrderResponse(
        id = mongoOrder.id.toString(),
        carId = mongoOrder.carId.toString(),
        userId = mongoOrder.userId.toString(),
        from = mongoOrder.from,
        to = mongoOrder.to,
        price = mongoCar.price,
    )

    fun emptyResponseOrder() = OrderResponse(
        id = "",
        carId = "",
        userId = "",
        from = null,
        to = null,
        price = BigDecimal.ZERO,
    )

    fun responseAggregatedOrder(mongoOrder: AggregatedMongoOrder, mongoCar: MongoCar) = AggregatedOrderResponse(
        id = mongoOrder.id.toString(),
        car = responseCar(mongoOrder.car!!),
        user = responseUser(mongoOrder.user!!),
        from = mongoOrder.from,
        to = mongoOrder.to,
        price = mongoCar.price,
    )

    fun emptyResponseAggregatedOrder() = AggregatedOrderResponse(
        id = "",
        car = CarResponse.from(MongoCar()),
        user = UserResponse.from(MongoUser()),
        from = null,
        to = null,
        price = BigDecimal.ZERO,
    )

    fun createOrderRequest(mongoCar: MongoCar, mongoUser: MongoUser) = CreateOrderRequest(
        carId = mongoCar.id.toString(),
        userId = mongoUser.id.toString(),
        from = monthAfter,
        to = monthAndDayAfter,
    )

    fun createOrderEntity(request: CreateOrderRequest) = MongoOrder(
        id = null,
        carId = ObjectId(request.carId),
        userId = ObjectId(request.userId),
        from = request.from,
        to = request.to,
    )

    fun createdOrder(mongoOrder: MongoOrder) = mongoOrder.copy(id = ObjectId())

    fun updateOrderRequest() = UpdateOrderRequest(
        from = tomorrow,
        to = twoDaysAfter,
    )

    fun orderPatch(request: UpdateOrderRequest) = MongoOrderPatch(
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
