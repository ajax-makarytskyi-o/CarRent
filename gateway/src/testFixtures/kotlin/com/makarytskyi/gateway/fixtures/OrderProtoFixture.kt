package com.makarytskyi.gateway.fixtures

import com.makarytskyi.commonmodels.error.Error
import com.makarytskyi.commonmodels.order.AggregatedOrder
import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.core.dto.order.AggregatedOrderResponseDto
import com.makarytskyi.core.dto.order.CreateOrderRequestDto
import com.makarytskyi.core.dto.order.OrderResponseDto
import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.gateway.fixtures.CarProtoFixture.randomCar
import com.makarytskyi.gateway.fixtures.UserProtoFixture.randomUser
import com.makarytskyi.gateway.fixtures.request.OrderRequestFixture.tomorrow
import com.makarytskyi.gateway.fixtures.request.OrderRequestFixture.twoDaysAfter
import com.makarytskyi.gateway.mapper.toResponse
import com.makarytskyi.gateway.util.Util.dateToTimestamp
import com.makarytskyi.gateway.util.Util.timestampToDate
import com.makarytskyi.grpcapi.input.reqreply.order.StreamCreatedOrdersByCar.StreamCreatedOrdersByUserIdRequest
import com.makarytskyi.grpcapi.input.reqreply.order.StreamCreatedOrdersByCar.StreamCreatedOrdersByUserIdResponse
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderResponse
import kotlin.random.Random
import org.bson.types.ObjectId
import com.makarytskyi.grpcapi.input.reqreply.order.CreateOrderResponse as GrpcCreateOrderResponse
import com.makarytskyi.grpcapi.input.reqreply.order.GetByIdOrderRequest as GrpcGetByIdOrderRequest
import com.makarytskyi.grpcapi.input.reqreply.order.GetByIdOrderResponse as GrpcGetByIdOrderResponse

object OrderProtoFixture {
    fun createRequest(request: CreateOrderRequestDto): CreateOrderRequest = CreateOrderRequest.newBuilder()
        .apply {
            orderBuilder.apply {
                setCarId(request.carId)
                setUserId(request.userId)
                setFrom(dateToTimestamp(request.from)).build()
                setTo(dateToTimestamp(request.to)).build()
            }
        }
        .build()

    fun streamCreatedOrdersRequest(userId: String): StreamCreatedOrdersByUserIdRequest =
        StreamCreatedOrdersByUserIdRequest.newBuilder().also {
            it.userId = userId
        }.build()

    fun patchRequest(id: String, request: UpdateOrderRequestDto): UpdateOrderRequest =
        UpdateOrderRequest.newBuilder()
            .apply {
                setId(id)
                updateBuilder.setStartDate(dateToTimestamp(request.from!!)).build()
                updateBuilder.setEndDate(dateToTimestamp(request.to!!)).build()
            }
            .build()

    fun deleteRequest(id: String): DeleteOrderRequest = DeleteOrderRequest
        .newBuilder()
        .setId(id)
        .build()

    fun createOrderResponse(request: CreateOrderRequestDto, price: Double) = OrderResponseDto(
        id = ObjectId().toString(),
        carId = request.carId,
        userId = request.userId,
        from = request.from,
        to = request.to,
        price = price.toBigDecimal(),
    )

    fun updateOrderResponse(request: UpdateOrderRequestDto, price: Double) = OrderResponseDto(
        id = ObjectId().toString(),
        carId = ObjectId().toString(),
        userId = ObjectId().toString(),
        from = request.from!!,
        to = request.to!!,
        price = price.toBigDecimal(),
    )

    fun findAllResponse(): FindAllOrdersResponse = FindAllOrdersResponse
        .newBuilder()
        .apply { successBuilder.addOrders(randomAggregatedOrder()) }
        .build()

    fun streamCreatedOrderResponse(): StreamCreatedOrdersByUserIdResponse = StreamCreatedOrdersByUserIdResponse
        .newBuilder()
        .apply {
            orderBuilder.from = dateToTimestamp(tomorrow)
            orderBuilder.to = dateToTimestamp(twoDaysAfter)
            orderBuilder.userId = ObjectId().toString()
            orderBuilder.carId = ObjectId().toString()
            orderBuilder.price = randomPrice()
        }
        .build()

    fun aggregatedOrderDto(protoResponse: GetByIdOrderResponse) = AggregatedOrderResponseDto(
        id = protoResponse.success.order.id,
        car = protoResponse.success.order.car.toResponse(),
        user = protoResponse.success.order.user.toResponse(),
        from = timestampToDate(protoResponse.success.order.from),
        to = timestampToDate(protoResponse.success.order.to),
        price = protoResponse.success.order.price.toBigDecimal(),
    )

    fun listOfAggregatedOrderDto(protoResponse: FindAllOrdersResponse): List<AggregatedOrderResponseDto> =
        protoResponse.success.ordersList.map {
            AggregatedOrderResponseDto(
                id = it.id,
                car = it.car.toResponse(),
                user = it.user.toResponse(),
                from = timestampToDate(it.from),
                to = timestampToDate(it.to),
                price = it.price.toBigDecimal(),
            )
        }

    fun successfulUpdateResponse(request: UpdateOrderRequestDto, price: Double): UpdateOrderResponse =
        UpdateOrderResponse.newBuilder()
            .apply {
                successBuilder.orderBuilder.apply {
                    setId(ObjectId().toString())
                    setCarId(ObjectId().toString())
                    setUserId(ObjectId().toString())
                    setFrom(dateToTimestamp(request.from!!)).build()
                    setTo(dateToTimestamp(request.to!!)).build()
                    setPrice(price)
                }
            }
            .build()

    fun failurePatchResponse(exception: Exception): UpdateOrderResponse = UpdateOrderResponse.newBuilder()
        .apply {
            failureBuilder.apply {
                setMessage(message)
                when (exception) {
                    is NotFoundException -> setNotFound(Error.getDefaultInstance())
                    is IllegalArgumentException -> setIllegalArgument(Error.getDefaultInstance())
                }
            }
        }
        .build()

    fun successfulCreateResponse(request: CreateOrderRequestDto, price: Double): CreateOrderResponse =
        CreateOrderResponse.newBuilder()
            .apply {
                successBuilder.orderBuilder.apply {
                    setId(ObjectId().toString())
                    setCarId(request.carId)
                    setUserId(request.userId)
                    setFrom(dateToTimestamp(request.from)).build()
                    setTo(dateToTimestamp(request.to)).build()
                    setPrice(price)
                }
            }
            .build()

    fun failureCreateResponse(exception: Exception): CreateOrderResponse = CreateOrderResponse
        .newBuilder()
        .apply {
            failureBuilder.apply {
                setMessage(message)
                when (exception) {
                    is NotFoundException -> setNotFound(Error.getDefaultInstance())
                    is IllegalArgumentException -> setIllegalArgument(Error.getDefaultInstance())
                }
            }
        }
        .build()

    fun successfulGetByIdResponse(order: AggregatedOrder): GetByIdOrderResponse = GetByIdOrderResponse
        .newBuilder()
        .apply { successBuilder.setOrder(order).build() }
        .build()

    fun failureGetByIdResponse(exception: Exception): GetByIdOrderResponse = GetByIdOrderResponse
        .newBuilder()
        .apply {
            failureBuilder.apply {
                setMessage(message)
                when (exception) {
                    is NotFoundException -> setNotFound(Error.getDefaultInstance())
                }
            }
        }
        .build()

    fun randomAggregatedOrder(): AggregatedOrder = AggregatedOrder.newBuilder()
        .apply {
            setId(ObjectId().toString())
            setCar(randomCar())
            setUser(randomUser())
            setPrice(Random.nextDouble(100.0, 500.0))
        }
        .build()

    fun aggregatedOrderResponse(order: AggregatedOrder): AggregatedOrderResponseDto = AggregatedOrderResponseDto(
        id = order.id,
        car = order.car.toResponse(),
        user = order.user.toResponse(),
        from = timestampToDate(order.from),
        to = timestampToDate(order.to),
        price = order.price.toBigDecimal()
    )

    fun successfulGrpcGetByIdResponse(order: AggregatedOrder): GrpcGetByIdOrderResponse = GrpcGetByIdOrderResponse
        .newBuilder()
        .apply { successBuilder.setOrder(order).build() }
        .build()

    fun grpcGetFullByIdRequest(id: String): GrpcGetByIdOrderRequest = GrpcGetByIdOrderRequest
        .newBuilder().also { it.id = id }
        .build()

    fun getFullByIdRequest(id: String): GetByIdOrderRequest = GetByIdOrderRequest
        .newBuilder().also { it.id = id }
        .build()

    fun successfulGetFullByIdRandomResponse(): GetByIdOrderResponse = GetByIdOrderResponse
        .newBuilder().also {
            it.successBuilder.orderBuilder.also { order ->
                order.id = ObjectId().toString()
                order.from = dateToTimestamp(tomorrow)
                order.to = dateToTimestamp(twoDaysAfter)
                order.car = randomCar()
                order.user = randomUser()
            }
        }
        .build()

    fun successfulCreateRandomResponse(): CreateOrderResponse = CreateOrderResponse
        .newBuilder().also {
            it.successBuilder.orderBuilder.also { order ->
                order.id = ObjectId().toString()
                order.from = dateToTimestamp(tomorrow)
                order.to = dateToTimestamp(twoDaysAfter)
                order.carId = ObjectId().toString()
                order.userId = ObjectId().toString()
            }
        }.build()

    fun successfulGrpcCreateResponse(order: Order): GrpcCreateOrderResponse = GrpcCreateOrderResponse
        .newBuilder()
        .apply { successBuilder.setOrder(order).build() }
        .build()

    fun failureGetFullByIdRandomResponse(exception: Exception): GetByIdOrderResponse = GetByIdOrderResponse
        .newBuilder().also {
            it.failureBuilder.also {
                it.message = exception.message
                it.notFoundBuilder
            }
        }
        .build()

    fun createOrderGrpcResponse(id: String, request: CreateOrderRequestDto, price: Double): GrpcCreateOrderResponse =
        GrpcCreateOrderResponse.newBuilder().also {
            it.successBuilder.orderBuilder.also { order ->
                order.id = id
                order.from = dateToTimestamp(request.from)
                order.to = dateToTimestamp(request.to)
                order.carId = request.carId
                order.userId = request.userId
                order.price = price
            }
        }
            .build()

    fun randomPrice(): Double = Random.nextDouble(100.0, 1000.0)
}
