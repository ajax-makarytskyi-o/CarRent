package fixtures

import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import com.makarytskyi.rentcar.model.Car
import com.makarytskyi.rentcar.model.Order
import com.makarytskyi.rentcar.model.User
import java.util.Calendar
import java.util.Date
import org.bson.types.ObjectId

object OrderFixture {
    val orderId = ObjectId().toHexString()
    val createdOrderId = ObjectId().toHexString()
    var tommorow = Calendar.getInstance()
    var twoDaysAfter = Calendar.getInstance()
    var monthAfter = Calendar.getInstance()
    var monthAndDayAfter = Calendar.getInstance()

    init {
        tommorow.add(Calendar.DAY_OF_YEAR, 1)
        twoDaysAfter.add(Calendar.DAY_OF_YEAR, 2)
        monthAfter.add(Calendar.DAY_OF_YEAR, 30)
        monthAndDayAfter.add(Calendar.DAY_OF_YEAR, 31)

    }

    fun existingOrder(car: Car, user: User) = Order(
        id = orderId,
        carId = car.id,
        userId = user.id,
        from = Date.from(tommorow.toInstant()),
        to = Date.from(twoDaysAfter.toInstant()),
    )

    fun existingOrderOnCar(car: Car, user: User) = Order(
        id = ObjectId().toHexString(),
        carId = car.id,
        userId = user.id,
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
    )

    fun responseOrder(order: Order, car: Car) = OrderResponse(
        id = order.id ?: "",
        carId = order.carId ?: "",
        userId = order.userId ?: "",
        from = order.from,
        to = order.to,
        price = car.price?.toLong(),
    )

    fun createOrderRequest(car: Car, user: User) = CreateOrderRequest(
        carId = car.id ?: "",
        userId = user.id ?: "",
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
    )

    fun createOrderEntity(request: CreateOrderRequest) = Order(
        id = null,
        carId = request.carId,
        userId = request.userId,
        from = request.from,
        to = request.to,
    )

    fun createdOrder(order: Order) = order.copy(id = createdOrderId)

    fun createdOrderResponse(order: Order, car: Car) = OrderResponse(
        id = order.id ?: "",
        carId = order.carId ?: "",
        userId = order.userId ?: "",
        from = order.from ?: Date.from(monthAfter.toInstant()),
        to = order.to ?: Date.from(monthAndDayAfter.toInstant()),
        price = car.price?.toLong(),
    )

    fun updateOrderRequest() = UpdateOrderRequest(
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
    )

    fun updateOrderEntity(request: UpdateOrderRequest) = Order(
        id = null,
        carId = null,
        userId = null,
        from = request.from,
        to = request.to,
    )

    fun updatedOrder(oldOrder: Order, request: UpdateOrderRequest) = oldOrder.copy(from = request.from, to = request.to)

    fun updatedOrderResponse(order: Order, car: Car) = OrderResponse(
        id = order.id ?: "",
        carId = order.carId ?: "",
        userId = order.userId ?: "",
        from = Date.from(tommorow.toInstant()),
        to = Date.from(twoDaysAfter.toInstant()),
        price = car.price?.toLong(),
    )
}
