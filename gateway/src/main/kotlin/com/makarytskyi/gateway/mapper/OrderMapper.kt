package com.makarytskyi.gateway.mapper

import com.google.protobuf.Timestamp
import com.makarytskyi.commonmodels.order.AggregatedOrder
import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.commonmodels.order.OrderUpdate
import com.makarytskyi.core.dto.order.AggregatedOrderResponseDto
import com.makarytskyi.core.dto.order.CreateOrderRequestDto
import com.makarytskyi.core.dto.order.OrderResponseDto
import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.core.exception.UnspecifiedServiceException
import com.makarytskyi.gateway.util.Util.timestampToDate
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderResponse
import java.util.Date
import com.makarytskyi.grpcapi.input.reqreply.order.CreateOrderRequest as GrpcCreateOrderRequest
import com.makarytskyi.grpcapi.input.reqreply.order.CreateOrderResponse as GrpcCreateOrderResponse
import com.makarytskyi.grpcapi.input.reqreply.order.GetByIdOrderRequest as GrpcGetByIdOrderRequest
import com.makarytskyi.grpcapi.input.reqreply.order.GetByIdOrderResponse as GrpcGetByIdOrderResponse

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
        from = timestampToDate(from),
        to = timestampToDate(to),
        price = price.toBigDecimal()
    )

    fun UpdateOrderRequestDto.toProto(): OrderUpdate = OrderUpdate.newBuilder()
        .setStartDate(dateToTimestamp(from))
        .setEndDate(dateToTimestamp(to))
        .build()

    fun CreateOrderResponse.toDto() =
        when (responseCase) {
            CreateOrderResponse.ResponseCase.SUCCESS -> success.order.toDto()
            CreateOrderResponse.ResponseCase.FAILURE -> failure.throwException()
            CreateOrderResponse.ResponseCase.RESPONSE_NOT_SET -> throw UnspecifiedServiceException(failure.message)
        }

    fun GetByIdOrderResponse.toDto() =
        when (responseCase) {
            GetByIdOrderResponse.ResponseCase.SUCCESS -> success.order.toDto()
            GetByIdOrderResponse.ResponseCase.FAILURE -> failure.throwException()
            GetByIdOrderResponse.ResponseCase.RESPONSE_NOT_SET -> throw UnspecifiedServiceException(failure.message)
        }

    fun UpdateOrderResponse.toDto(): OrderResponseDto =
        when (responseCase) {
            UpdateOrderResponse.ResponseCase.SUCCESS -> success.order.toDto()
            UpdateOrderResponse.ResponseCase.FAILURE -> failure.throwException()
            UpdateOrderResponse.ResponseCase.RESPONSE_NOT_SET -> throw UnspecifiedServiceException(failure.message)
        }

    fun FindAllOrdersResponse.toDto(): List<AggregatedOrderResponseDto> =
        when (responseCase) {
            FindAllOrdersResponse.ResponseCase.SUCCESS -> success.ordersList.stream().map { it.toDto() }.toList()
            else -> throw UnspecifiedServiceException(failure.message)
        }

    fun GrpcGetByIdOrderRequest.toInternalProto(): GetByIdOrderRequest = GetByIdOrderRequest
        .newBuilder().also {
            it.id = this.id
        }
        .build()

    fun GetByIdOrderResponse.toGrpcProto(): GrpcGetByIdOrderResponse = GrpcGetByIdOrderResponse
        .newBuilder().also {
            when (this.responseCase) {
                GetByIdOrderResponse.ResponseCase.SUCCESS -> it.successBuilder.order = this.success.order
                GetByIdOrderResponse.ResponseCase.FAILURE -> this.failure.throwException()
                GetByIdOrderResponse.ResponseCase.RESPONSE_NOT_SET ->
                    throw UnspecifiedServiceException(this.failure.message)
            }
        }
        .build()

    fun GrpcCreateOrderRequest.toInternalProto(): CreateOrderRequest = CreateOrderRequest.newBuilder()
        .also {
            it.order = this.order
        }
        .build()

    fun CreateOrderResponse.toGrpcProto(): GrpcCreateOrderResponse = GrpcCreateOrderResponse
        .newBuilder().also {
            when (this.responseCase) {
                CreateOrderResponse.ResponseCase.SUCCESS -> it.successBuilder.order = this.success.order
                CreateOrderResponse.ResponseCase.FAILURE -> this.failure.throwException()
                CreateOrderResponse.ResponseCase.RESPONSE_NOT_SET ->
                    throw UnspecifiedServiceException(this.failure.message)
            }
        }
        .build()

    private fun CreateOrderResponse.Failure.throwException(): Nothing =
        when (errorCase) {
            CreateOrderResponse.Failure.ErrorCase.NOT_FOUND -> throw NotFoundException(message)
            CreateOrderResponse.Failure.ErrorCase.ILLEGAL_ARGUMENT -> throw IllegalArgumentException(message)
            CreateOrderResponse.Failure.ErrorCase.ERROR_NOT_SET -> throw UnspecifiedServiceException(message)
        }

    private fun UpdateOrderResponse.Failure.throwException(): Nothing =
        when (errorCase) {
            UpdateOrderResponse.Failure.ErrorCase.NOT_FOUND -> throw NotFoundException(message)
            UpdateOrderResponse.Failure.ErrorCase.ILLEGAL_ARGUMENT -> throw IllegalArgumentException(message)
            UpdateOrderResponse.Failure.ErrorCase.ERROR_NOT_SET -> throw UnspecifiedServiceException(message)
        }

    private fun GetByIdOrderResponse.Failure.throwException(): Nothing =
        when (errorCase) {
            GetByIdOrderResponse.Failure.ErrorCase.NOT_FOUND -> throw NotFoundException(message)
            GetByIdOrderResponse.Failure.ErrorCase.ERROR_NOT_SET -> throw UnspecifiedServiceException(message)
        }

    private fun Order.toDto() = OrderResponseDto(
        id = id,
        carId = carId,
        userId = userId,
        from = timestampToDate(from),
        to = timestampToDate(to),
        price = price.toBigDecimal(),
    )

    private fun dateToTimestamp(date: Date?): Timestamp =
        Timestamp.newBuilder()
            .setSeconds(date?.time?.div(MILLIS_IN_SECOND) ?: 0)
            .build()

    private const val MILLIS_IN_SECOND = 1000
}
