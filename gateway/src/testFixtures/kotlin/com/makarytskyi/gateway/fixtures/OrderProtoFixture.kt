package com.makarytskyi.gateway.fixtures

import com.google.protobuf.Timestamp
import com.makarytskyi.core.dto.order.AggregatedOrderResponseDto
import com.makarytskyi.core.dto.order.CreateOrderRequestDto
import com.makarytskyi.core.dto.order.OrderResponseDto
import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.gateway.fixtures.CarProtoFixture.randomCar
import com.makarytskyi.gateway.fixtures.UserProtoFixture.randomUser
import com.makarytskyi.gateway.mapper.toResponse
import com.makarytskyi.internalapi.commonmodels.error.Error
import com.makarytskyi.internalapi.commonmodels.order.AggregatedOrder
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.PatchOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.PatchOrderResponse
import java.util.Date
import kotlin.random.Random
import org.bson.types.ObjectId

object OrderProtoFixture {
    fun createRequest(request: CreateOrderRequestDto): CreateOrderRequest = CreateOrderRequest.newBuilder()
        .apply {
            orderBuilder.apply {
                setCarId(request.carId)
                setUserId(request.userId)
                setFrom(Timestamp.newBuilder().setSeconds(request.from.time).build())
                setTo(Timestamp.newBuilder().setSeconds(request.to.time).build())
            }
        }
        .build()

    fun patchRequest(id: String, request: UpdateOrderRequestDto): PatchOrderRequest =
        PatchOrderRequest.newBuilder()
            .apply {
                setId(id)
                patchBuilder.setStartDate(Timestamp.newBuilder().setSeconds(request.from!!.time).build())
                patchBuilder.setEndDate(Timestamp.newBuilder().setSeconds(request.to!!.time).build())
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

    fun aggregatedOrderDto(protoResponse: GetByIdOrderResponse) = AggregatedOrderResponseDto(
        id = protoResponse.success.order.id,
        car = protoResponse.success.order.car.toResponse(),
        user = protoResponse.success.order.user.toResponse(),
        from = Date(protoResponse.success.order.from.seconds),
        to = Date(protoResponse.success.order.to.seconds),
        price = protoResponse.success.order.price.toBigDecimal(),
    )

    fun listOfAggregatedOrderDto(protoResponse: FindAllOrdersResponse): List<AggregatedOrderResponseDto> =
        protoResponse.success.ordersList.map {
            AggregatedOrderResponseDto(
                id = it.id,
                car = it.car.toResponse(),
                user = it.user.toResponse(),
                from = Date(it.from.seconds),
                to = Date(it.to.seconds),
                price = it.price.toBigDecimal(),
            )
        }

    fun successfulUpdateResponse(request: UpdateOrderRequestDto, price: Double): PatchOrderResponse =
        PatchOrderResponse.newBuilder()
            .apply {
                successBuilder.orderBuilder.apply {
                    setId(ObjectId().toString())
                    setCarId(ObjectId().toString())
                    setUserId(ObjectId().toString())
                    setFrom(Timestamp.newBuilder().setSeconds(request.from!!.time).build())
                    setTo(Timestamp.newBuilder().setSeconds(request.to!!.time).build())
                    setPrice(price)
                }
            }
            .build()

    fun failurePatchResponse(exception: Exception): PatchOrderResponse = PatchOrderResponse.newBuilder()
        .apply {
            failureBuilder.apply {
                setMessage(this.message)
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
                    setFrom(Timestamp.newBuilder().setSeconds(request.from.time).build())
                    setTo(Timestamp.newBuilder().setSeconds(request.to.time).build())
                    setPrice(price)
                }
            }
            .build()

    fun failureCreateResponse(exception: Exception): CreateOrderResponse = CreateOrderResponse
        .newBuilder()
        .apply {
            failureBuilder.apply {
                setMessage(this.message)
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
                setMessage(this.message)
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
        from = Date(order.from.seconds),
        to = Date(order.to.seconds),
        price = order.price.toBigDecimal()
    )
}
