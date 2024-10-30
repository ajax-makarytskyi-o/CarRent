package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.dto.order.AggregatedOrderResponseDto
import com.makarytskyi.core.dto.order.CreateOrderRequestDto
import com.makarytskyi.core.dto.order.OrderResponseDto
import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import com.makarytskyi.internalapi.commonmodels.order.AggregatedOrder
import com.makarytskyi.internalapi.commonmodels.order.Order
import com.makarytskyi.internalapi.commonmodels.order.Patch
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.PatchOrderResponse
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

fun OrderResponseDto.toCreateResponse(): CreateOrderResponse = CreateOrderResponse.newBuilder()
    .also { it.successBuilder.setOrder(this.toProto()) }
    .build()

fun List<AggregatedOrder>.toFindAllResponse(): FindAllOrdersResponse = FindAllOrdersResponse.newBuilder()
    .also { it.successBuilder.addAllOrders(this) }.build()

fun AggregatedOrderResponseDto.toGetByIdResponse(): GetByIdOrderResponse = GetByIdOrderResponse.newBuilder()
    .also { it.successBuilder.setOrder(this.toProto()) }.build()

fun OrderResponseDto.toPatchResponse(): PatchOrderResponse = PatchOrderResponse.newBuilder()
    .also { it.successBuilder.setOrder(this.toProto()) }.build()

fun toDeleteResponse(): DeleteOrderResponse = DeleteOrderResponse.newBuilder()
    .apply { successBuilder }.build()

fun CreateOrderRequest.toDto() = CreateOrderRequestDto(
    carId = order.carId,
    userId = order.userId,
    from = Date(order.from.seconds),
    to = Date(order.to.seconds),
)

fun Patch.toDto(): UpdateOrderRequestDto = UpdateOrderRequestDto(
    from = timestampToDate(from),
    to = timestampToDate(to),
)

fun OrderResponseDto.toProto(): Order = Order.newBuilder()
    .apply {
        setId(this@toProto.id)
        setCarId(this@toProto.carId)
        setUserId(this@toProto.userId)
        setFrom(dateToTimestamp(this@toProto.from))
        setTo(dateToTimestamp(this@toProto.to))
        setPrice(this@toProto.price.toDouble())
    }
    .build()

fun AggregatedOrderResponseDto.toProto(): AggregatedOrder = AggregatedOrder.newBuilder()
    .apply {
        setId(this@toProto.id)
        setCar(this@toProto.car.toProto())
        setUser(this@toProto.user.toProto())
        setFrom(dateToTimestamp(this@toProto.from))
        setTo(dateToTimestamp(this@toProto.to))
        setPrice(this@toProto.price.toDouble())
    }
    .build()

fun AggregatedMongoOrder.toResponse(): AggregatedOrderResponseDto {
    val bookedDays = from?.toInstant()?.until(to?.toInstant(), ChronoUnit.DAYS)?.toBigDecimal() ?: BigDecimal.ZERO

    return AggregatedOrderResponseDto(
        id = id?.toString().orEmpty(),
        car = (car ?: MongoCar()).toResponse(),
        user = (user ?: MongoUser()).toResponse(),
        from = from ?: throw IllegalArgumentException("Start date of order is unset"),
        to = to ?: throw IllegalArgumentException("End date of order is unset"),
        price = car?.price?.times(bookedDays) ?: BigDecimal.ZERO,
    )
}

fun CreateOrderRequestDto.toEntity(): MongoOrder = MongoOrder(
    carId = ObjectId(carId),
    userId = ObjectId(userId),
    from = from,
    to = to,
)

fun MongoOrder.toResponse(price: BigDecimal?): OrderResponseDto {
    val bookedDays = from?.toInstant()?.until(to?.toInstant(), ChronoUnit.DAYS)?.toBigDecimal() ?: BigDecimal.ZERO

    return OrderResponseDto(
        id = id?.toString().orEmpty(),
        carId = carId?.toString().orEmpty(),
        userId = userId?.toString().orEmpty(),
        from = from ?: throw IllegalArgumentException("Start date of order is unset"),
        to = to ?: throw IllegalArgumentException("End date of order is unset"),
        price = price?.times(bookedDays) ?: BigDecimal.ZERO
    )
}

fun UpdateOrderRequestDto.toPatch(): MongoOrderPatch = MongoOrderPatch(
    from = from,
    to = to,
)
