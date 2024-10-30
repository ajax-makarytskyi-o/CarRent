package com.makarytskyi.gateway.mapper

import com.google.protobuf.Timestamp
import com.makarytskyi.core.dto.order.AggregatedOrderResponseDto
import com.makarytskyi.core.dto.order.CreateOrderRequestDto
import com.makarytskyi.core.dto.order.OrderResponseDto
import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.commonmodels.order.AggregatedOrder
import com.makarytskyi.internalapi.commonmodels.order.Order
import com.makarytskyi.internalapi.commonmodels.order.Patch
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.PatchOrderResponse
import java.util.Date

fun CreateOrderRequestDto.toProto(): CreateOrderRequest = CreateOrderRequest.newBuilder()
    .apply {
        orderBuilder.apply {
            setCarId(this@toProto.carId)
            setUserId(this@toProto.userId)
            setFrom(dateToTimestamp(this@toProto.from))
            setTo(dateToTimestamp(this@toProto.to))
        }
    }
    .build()

fun AggregatedOrder.toDto(): AggregatedOrderResponseDto = AggregatedOrderResponseDto(
    id = id,
    car = car.toResponse(),
    user = user.toResponse(),
    from = Date(from.seconds),
    to = Date(to.seconds),
    price = price.toBigDecimal()
)

fun FindAllOrdersResponse.toDto(): List<AggregatedOrderResponseDto> =
    success.ordersList.stream().map { it.toDto() }.toList()

fun UpdateOrderRequestDto.toProto(): Patch = Patch.newBuilder()
    .setFrom(dateToTimestamp(from))
    .setTo(dateToTimestamp(to))
    .build()

@SuppressWarnings("TooGenericExceptionThrown")
fun CreateOrderResponse.toDto() =
    when (responseCase) {
        CreateOrderResponse.ResponseCase.SUCCESS -> success.order.toDto()
        CreateOrderResponse.ResponseCase.FAILURE -> failure.throwException()
        CreateOrderResponse.ResponseCase.RESPONSE_NOT_SET -> throw Exception(failure.message)
    }

@SuppressWarnings("TooGenericExceptionThrown")
fun GetByIdOrderResponse.toDto() =
    when (responseCase) {
        GetByIdOrderResponse.ResponseCase.SUCCESS -> success.order.toDto()
        GetByIdOrderResponse.ResponseCase.FAILURE -> failure.throwException()
        GetByIdOrderResponse.ResponseCase.RESPONSE_NOT_SET -> throw Exception(failure.message)
    }

@SuppressWarnings("TooGenericExceptionThrown")
fun PatchOrderResponse.toDto(): OrderResponseDto =
    when (responseCase) {
        PatchOrderResponse.ResponseCase.SUCCESS -> success.order.toDto()
        PatchOrderResponse.ResponseCase.FAILURE -> failure.throwException()
        PatchOrderResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException(failure.message)
    }

@SuppressWarnings("TooGenericExceptionThrown")
private fun CreateOrderResponse.Failure.throwException(): Nothing =
    when (errorCase) {
        CreateOrderResponse.Failure.ErrorCase.NOT_FOUND -> throw NotFoundException(message)
        CreateOrderResponse.Failure.ErrorCase.ILLEGAL_ARGUMENT -> throw IllegalArgumentException(message)
        CreateOrderResponse.Failure.ErrorCase.ERROR_NOT_SET -> throw RuntimeException(message)
    }

@SuppressWarnings("TooGenericExceptionThrown")
private fun PatchOrderResponse.Failure.throwException(): Nothing =
    when (errorCase) {
        PatchOrderResponse.Failure.ErrorCase.NOT_FOUND -> throw NotFoundException(message)
        PatchOrderResponse.Failure.ErrorCase.ILLEGAL_ARGUMENT -> throw IllegalArgumentException(message)
        PatchOrderResponse.Failure.ErrorCase.ERROR_NOT_SET -> throw RuntimeException(message)
    }

@SuppressWarnings("TooGenericExceptionThrown")
private fun GetByIdOrderResponse.Failure.throwException(): Nothing =
    when (errorCase) {
        GetByIdOrderResponse.Failure.ErrorCase.NOT_FOUND -> throw NotFoundException(message)
        GetByIdOrderResponse.Failure.ErrorCase.ERROR_NOT_SET -> throw RuntimeException(message)
    }

private fun Order.toDto() = OrderResponseDto(
    id = this.id,
    carId = this.carId,
    userId = this.userId,
    from = Date(this.from.seconds),
    to = Date(this.to.seconds),
    price = this.price.toBigDecimal(),
)

private fun dateToTimestamp(date: Date?): Timestamp =
    Timestamp.newBuilder()
        .setSeconds(date?.time ?: 0)
        .build()
