package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.dto.order.AggregatedOrderResponseDto
import com.makarytskyi.core.dto.order.CreateOrderRequestDto
import com.makarytskyi.core.dto.order.OrderResponseDto
import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import com.makarytskyi.internalapi.commonmodels.order.AggregatedOrder
import com.makarytskyi.internalapi.commonmodels.order.Order
import com.makarytskyi.internalapi.commonmodels.order.OrderUpdate
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderResponse
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

@SuppressWarnings("TooManyFunctions")
object OrderMapper {
    fun OrderResponseDto.toCreateResponse(): CreateOrderResponse = CreateOrderResponse.newBuilder()
        .also { it.successBuilder.setOrder(this.toProto()) }
        .build()

    fun List<AggregatedOrderResponseDto>.toFindAllResponse(): FindAllOrdersResponse = FindAllOrdersResponse.newBuilder()
        .also { it.successBuilder.addAllOrders(this.map { responseDto -> responseDto.toProto() }) }.build()

    fun AggregatedOrderResponseDto.toGetByIdResponse(): GetByIdOrderResponse = GetByIdOrderResponse.newBuilder()
        .also { it.successBuilder.setOrder(this.toProto()) }.build()

    fun OrderResponseDto.toPatchResponse(): UpdateOrderResponse = UpdateOrderResponse.newBuilder()
        .also { it.successBuilder.setOrder(this.toProto()) }.build()

    fun toDeleteFailureResponse(): DeleteOrderResponse = DeleteOrderResponse.newBuilder()
        .apply { successBuilder }.build()

    fun CreateOrderRequest.toDto() = CreateOrderRequestDto(
        carId = order.carId,
        userId = order.userId,
        from = Date(order.from.seconds),
        to = Date(order.to.seconds),
    )

    fun OrderUpdate.toDto(): UpdateOrderRequestDto = UpdateOrderRequestDto(
        from = if (this.hasStartDate()) timestampToDate(startDate) else null,
        to = if (this.hasEndDate()) timestampToDate(endDate) else null,
    )

    fun OrderResponseDto.toProto(): Order = Order.newBuilder()
        .also {
            it.setId(id)
            it.setCarId(carId)
            it.setUserId(userId)
            it.setFrom(dateToTimestamp(from))
            it.setTo(dateToTimestamp(to))
            it.setPrice(price.toDouble())
        }
        .build()

    fun AggregatedOrderResponseDto.toProto(): AggregatedOrder = AggregatedOrder.newBuilder()
        .also {
            it.setId(id)
            it.setCar(car.toProto())
            it.setUser(user.toProto())
            it.setFrom(dateToTimestamp(from))
            it.setTo(dateToTimestamp(to))
            it.setPrice(price.toDouble())
        }
        .build()

    fun AggregatedMongoOrder.toResponse(): AggregatedOrderResponseDto {
        val bookedDays = from?.toInstant()?.until(to?.toInstant(), ChronoUnit.DAYS)?.toBigDecimal() ?: BigDecimal.ZERO

        return AggregatedOrderResponseDto(
            id = requireNotNull(id?.toString()) { "Order id is null" },
            car = (car ?: MongoCar()).toResponse(),
            user = (user ?: MongoUser()).toResponse(),
            from = requireNotNull(from) { "Start date of order is unset" },
            to = requireNotNull(to) { "End date of order is unset" },
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
            id = requireNotNull(id?.toString()) { "Order id is null" },
            carId = requireNotNull(carId?.toString()) { "Car id is null" },
            userId = requireNotNull(userId?.toString()) { "User id is null" },
            from = requireNotNull(from) { "Start date of order is unset" },
            to = requireNotNull(to) { "End date of order is unset" },
            price = price?.times(bookedDays) ?: BigDecimal.ZERO
        )
    }

    fun UpdateOrderRequestDto.toPatch(): MongoOrderPatch = MongoOrderPatch(
        from = from,
        to = to,
    )
}
