package com.makarytskyi.gateway.mapper

import com.google.protobuf.Timestamp
import com.makarytskyi.core.dto.order.AggregatedOrderResponse
import com.makarytskyi.core.dto.order.CreateOrderRequest
import com.makarytskyi.core.dto.order.OrderResponse
import com.makarytskyi.core.dto.order.UpdateOrderRequest
import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.error.Error
import com.makarytskyi.internalapi.error.ExceptionType
import com.makarytskyi.internalapi.model.order.AggregatedOrder
import com.makarytskyi.internalapi.model.order.Order
import com.makarytskyi.internalapi.model.order.Patch
import com.makarytskyi.internalapi.reqreply.create.CreateOrderProtoRequest
import com.makarytskyi.internalapi.reqreply.create.CreateOrderProtoResponse
import com.makarytskyi.rentcar.proto.reqreply.find_all.FindAllOrdersProtoResponse
import com.makarytskyi.rentcar.proto.reqreply.find_by_id.GetByIdOrderProtoResponse
import com.makarytskyi.rentcar.proto.reqreply.patch.PatchOrderProtoResponse
import java.util.Date

fun CreateOrderRequest.toProto(): CreateOrderProtoRequest = CreateOrderProtoRequest.newBuilder()
    .setOrder(
        Order.newBuilder()
            .setCarId(this.carId)
            .setUserId(this.userId)
            .setFrom(dateToTimestamp(this.from))
            .setTo(dateToTimestamp(this.to))
            .build()
    )
    .build()

fun AggregatedOrder.toDto(): AggregatedOrderResponse = AggregatedOrderResponse(
    id = id,
    car = car.toResponse(),
    user = user.toResponse(),
    from = Date(from.seconds),
    to = Date(to.seconds),
    price = price.toBigDecimal()
)

fun FindAllOrdersProtoResponse.toDto(): List<AggregatedOrderResponse> =
    success.ordersList.stream().map { it.toDto() }.toList()

fun CreateOrderProtoResponse.toDto() =
    when (responseCase) {
        CreateOrderProtoResponse.ResponseCase.SUCCESS -> OrderResponse(
            id = success.order.id,
            carId = success.order.carId,
            userId = success.order.userId,
            from = Date(success.order.from.seconds),
            to = Date(success.order.to.seconds),
            price = success.order.price.toBigDecimal(),
        )

        CreateOrderProtoResponse.ResponseCase.ERROR -> failure(error)
        CreateOrderProtoResponse.ResponseCase.RESPONSE_NOT_SET -> throw Exception(error.message)
    }

fun GetByIdOrderProtoResponse.toDto() =
    when (responseCase) {
        GetByIdOrderProtoResponse.ResponseCase.SUCCESS -> AggregatedOrderResponse(
            id = success.order.id,
            car = success.order.car.toResponse(),
            user = success.order.user.toResponse(),
            from = Date(success.order.from.seconds),
            to = Date(success.order.to.seconds),
            price = success.order.price.toBigDecimal(),
        )

        GetByIdOrderProtoResponse.ResponseCase.ERROR -> failure(this.error)
        GetByIdOrderProtoResponse.ResponseCase.RESPONSE_NOT_SET -> throw Exception(error.message)
    }

fun UpdateOrderRequest.toProto(): Patch = Patch.newBuilder()
    .setFrom(dateToTimestamp(from))
    .setTo(dateToTimestamp(to))
    .build()

fun PatchOrderProtoResponse.toDto(): OrderResponse =
    when (responseCase) {
        PatchOrderProtoResponse.ResponseCase.SUCCESS -> OrderResponse(
            id = success.order.id,
            carId = success.order.carId,
            userId = success.order.userId,
            from = Date(success.order.from.seconds),
            to = Date(success.order.to.seconds),
            price = success.order.price.toBigDecimal(),
        )

        PatchOrderProtoResponse.ResponseCase.ERROR -> failure(error)
        PatchOrderProtoResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException(error.message)
    }

private fun failure(error: Error): Nothing =
    when (error.exceptionType) {
        ExceptionType.NOT_FOUND -> throw NotFoundException(error.message)
        ExceptionType.ILLEGAL_ARGUMENT -> throw IllegalArgumentException(error.message)
        else -> throw RuntimeException(error.message)
    }

private fun dateToTimestamp(date: Date?): Timestamp =
    Timestamp.newBuilder()
        .setSeconds(date?.time ?: 0)
        .build()
