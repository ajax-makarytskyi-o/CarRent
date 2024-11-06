package com.makarytskyi.gateway.mapper

import com.google.protobuf.Timestamp
import com.makarytskyi.core.dto.order.AggregatedOrderResponseDto
import com.makarytskyi.core.dto.order.CreateOrderRequestDto
import com.makarytskyi.core.dto.order.OrderResponseDto
import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.commonmodels.order.AggregatedOrder
import com.makarytskyi.internalapi.commonmodels.order.Order
import com.makarytskyi.internalapi.commonmodels.order.OrderUpdate
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderResponse
import java.util.Date

@SuppressWarnings("TooManyFunctions")
object OrderMapper {
    fun CreateOrderRequestDto.toProto(): CreateOrderRequest = CreateOrderRequest.newBuilder()
        .also {
            it.orderBuilder.also { order ->
                order.setCarId(carId)
                order.setUserId(userId)
                order.setFrom(dateToTimestamp(from))
                order.setTo(dateToTimestamp(to))
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

    fun UpdateOrderRequestDto.toProto(): OrderUpdate = OrderUpdate.newBuilder()
        .setStartDate(dateToTimestamp(from))
        .setEndDate(dateToTimestamp(to))
        .build()

    @SuppressWarnings("TooGenericExceptionThrown")
    fun CreateOrderResponse.toDto() =
        when (responseCase) {
            CreateOrderResponse.ResponseCase.SUCCESS -> success.order.toDto()
            CreateOrderResponse.ResponseCase.FAILURE -> failure.throwException()
            CreateOrderResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException(failure.message)
        }

    @SuppressWarnings("TooGenericExceptionThrown")
    fun GetByIdOrderResponse.toDto() =
        when (responseCase) {
            GetByIdOrderResponse.ResponseCase.SUCCESS -> success.order.toDto()
            GetByIdOrderResponse.ResponseCase.FAILURE -> failure.throwException()
            GetByIdOrderResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException(failure.message)
        }

    @SuppressWarnings("TooGenericExceptionThrown")
    fun UpdateOrderResponse.toDto(): OrderResponseDto =
        when (responseCase) {
            UpdateOrderResponse.ResponseCase.SUCCESS -> success.order.toDto()
            UpdateOrderResponse.ResponseCase.FAILURE -> failure.throwException()
            UpdateOrderResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException(failure.message)
        }

    @SuppressWarnings("TooGenericExceptionThrown")
    fun FindAllOrdersResponse.toDto(): List<AggregatedOrderResponseDto> =
        when (responseCase) {
            FindAllOrdersResponse.ResponseCase.SUCCESS -> success.ordersList.stream().map { it.toDto() }.toList()
            else -> throw RuntimeException(failure.message)
        }

    @SuppressWarnings("TooGenericExceptionThrown")
    private fun CreateOrderResponse.Failure.throwException(): Nothing =
        when (errorCase) {
            CreateOrderResponse.Failure.ErrorCase.NOT_FOUND -> throw NotFoundException(message)
            CreateOrderResponse.Failure.ErrorCase.ILLEGAL_ARGUMENT -> throw IllegalArgumentException(message)
            CreateOrderResponse.Failure.ErrorCase.ERROR_NOT_SET -> throw RuntimeException(message)
        }

    @SuppressWarnings("TooGenericExceptionThrown")
    private fun UpdateOrderResponse.Failure.throwException(): Nothing =
        when (errorCase) {
            UpdateOrderResponse.Failure.ErrorCase.NOT_FOUND -> throw NotFoundException(message)
            UpdateOrderResponse.Failure.ErrorCase.ILLEGAL_ARGUMENT -> throw IllegalArgumentException(message)
            UpdateOrderResponse.Failure.ErrorCase.ERROR_NOT_SET -> throw RuntimeException(message)
        }

    @SuppressWarnings("TooGenericExceptionThrown")
    private fun GetByIdOrderResponse.Failure.throwException(): Nothing =
        when (errorCase) {
            GetByIdOrderResponse.Failure.ErrorCase.NOT_FOUND -> throw NotFoundException(message)
            GetByIdOrderResponse.Failure.ErrorCase.ERROR_NOT_SET -> throw RuntimeException(message)
        }

    private fun Order.toDto() = OrderResponseDto(
        id = id,
        carId = carId,
        userId = userId,
        from = Date(from.seconds),
        to = Date(to.seconds),
        price = price.toBigDecimal(),
    )

    private fun dateToTimestamp(date: Date?): Timestamp =
        Timestamp.newBuilder()
            .setSeconds(date?.time ?: 0)
            .build()
}
