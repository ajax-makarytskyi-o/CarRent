package order

import car.CarFixture.carId
import user.UserFixture.userId
import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import com.makarytskyi.rentcar.model.Order
import java.util.Calendar
import java.util.Date

object OrderFixture {
    const val orderId: String = "5720057"
    const val createdOrderId: String = "7480257"
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

    val existingOrder = Order(
        id = orderId,
        carId = carId,
        userId = userId,
        from = Date.from(tommorow.toInstant()),
        to = Date.from(twoDaysAfter.toInstant()),
    )

    val existingOrderOnCar = Order(
        id = createdOrderId,
        carId = carId,
        userId = userId,
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
    )

    val responseOrder = OrderResponse(
        id = orderId,
        carId = carId,
        userId = userId,
        from = Date.from(tommorow.toInstant()),
        to = Date.from(twoDaysAfter.toInstant()),
        price = 150,
    )

    val createOrderRequest = CreateOrderRequest(
        carId = carId,
        userId = userId,
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
    )

    val createOrderEntity = Order(
        id = null,
        carId = carId,
        userId = userId,
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
    )

    val createdOrder = Order(
        id = createdOrderId,
        carId = carId,
        userId = userId,
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
    )

    val createdOrderResponse = OrderResponse(
        id = createdOrderId,
        carId = carId,
        userId = userId,
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
        price = 150,
    )

    val updateOrderRequest = UpdateOrderRequest(
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
    )

    val updateOrderEntity = Order(
        id = null,
        carId = null,
        userId = null,
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
    )

    val updatedOrder = Order(
        id = orderId,
        carId = carId,
        userId = userId,
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
    )

    val updatedOrderResponse = OrderResponse(
        id = orderId,
        carId = carId,
        userId = userId,
        from = Date.from(monthAfter.toInstant()),
        to = Date.from(monthAndDayAfter.toInstant()),
        price = 150,
    )
}
