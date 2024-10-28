package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.dto.order.AggregatedOrderResponse
import com.makarytskyi.core.dto.order.CreateOrderRequest
import com.makarytskyi.core.dto.order.OrderResponse
import com.makarytskyi.core.dto.order.UpdateOrderRequest
import com.makarytskyi.internalapi.model.order.AggregatedOrder
import com.makarytskyi.internalapi.model.order.Order
import com.makarytskyi.internalapi.model.order.Patch
import com.makarytskyi.internalapi.reqreply.create.CreateOrderProtoRequest
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoOrder
import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.model.patch.MongoOrderPatch
import com.makarytskyi.rentcar.model.projection.AggregatedMongoOrder
import com.makarytskyi.rentcar.util.dateToTimestamp
import com.makarytskyi.rentcar.util.timestampToDate
import java.math.BigDecimal
import java.time.temporal.ChronoUnit
import java.util.Date
import org.bson.types.ObjectId

fun CreateOrderProtoRequest.toDto() = CreateOrderRequest(
    carId = order.carId,
    userId = order.userId,
    from = Date(order.from.seconds),
    to = Date(order.to.seconds),
)

fun Patch.toDto(): UpdateOrderRequest = UpdateOrderRequest(
    from = timestampToDate(from),
    to = timestampToDate(to),
)

fun OrderResponse.toProto(): Order = Order.newBuilder()
    .setId(id)
    .setCarId(carId)
    .setUserId(userId)
    .setFrom(from?.let { dateToTimestamp(it) })
    .setTo(to?.let { dateToTimestamp(it) })
    .setPrice(price?.toDouble() ?: 0.0)
    .build()

fun AggregatedOrderResponse.toProto(): AggregatedOrder = AggregatedOrder.newBuilder()
    .setId(id)
    .setCar(car.toProto())
    .setUser(user.toProto())
    .setFrom(from?.let { dateToTimestamp(it) })
    .setTo(to?.let { dateToTimestamp(it) })
    .setPrice(price?.toDouble() ?: 0.0)
    .build()

fun AggregatedMongoOrder.toResponse(): AggregatedOrderResponse {
    val bookedDays = from?.toInstant()?.until(to?.toInstant(), ChronoUnit.DAYS)?.toBigDecimal() ?: BigDecimal.ZERO

    return AggregatedOrderResponse(
        id = id?.toString().orEmpty(),
        car = (car ?: MongoCar()).toResponse(),
        user = (user ?: MongoUser()).toResponse(),
        from = from,
        to = to,
        price = car?.price?.times(bookedDays) ?: BigDecimal.ZERO,
    )
}

fun CreateOrderRequest.toEntity(): MongoOrder = MongoOrder(
    carId = ObjectId(carId),
    userId = ObjectId(userId),
    from = from,
    to = to,
)

fun MongoOrder.toResponse(price: BigDecimal?): OrderResponse {
    val bookedDays = from?.toInstant()?.until(to?.toInstant(), ChronoUnit.DAYS)?.toBigDecimal() ?: BigDecimal.ZERO

    return OrderResponse(
        id = id?.toString().orEmpty(),
        carId = carId?.toString().orEmpty(),
        userId = userId?.toString().orEmpty(),
        from = from,
        to = to,
        price = price?.times(bookedDays) ?: BigDecimal.ZERO
    )
}

fun UpdateOrderRequest.toPatch(): MongoOrderPatch = MongoOrderPatch(
    from = from,
    to = to,
)
