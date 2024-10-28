package com.makarytskyi.gateway.fixtures

import com.google.protobuf.Timestamp
import com.makarytskyi.core.dto.order.AggregatedOrderResponse
import com.makarytskyi.core.dto.order.CreateOrderRequest
import com.makarytskyi.core.dto.order.OrderResponse
import com.makarytskyi.core.dto.order.UpdateOrderRequest
import com.makarytskyi.gateway.fixtures.CarProtoFixture.randomCar
import com.makarytskyi.gateway.fixtures.UserProtoFixture.randomUser
import com.makarytskyi.gateway.mapper.toResponse
import com.makarytskyi.internalapi.model.order.AggregatedOrder
import com.makarytskyi.internalapi.reqreply.create.CreateOrderProtoRequest
import com.makarytskyi.internalapi.reqreply.create.CreateOrderProtoResponse
import com.makarytskyi.rentcar.proto.reqreply.delete.DeleteOrderProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.find_all.FindAllOrdersProtoResponse
import com.makarytskyi.rentcar.proto.reqreply.find_by_id.GetByIdOrderProtoResponse
import com.makarytskyi.rentcar.proto.reqreply.patch.PatchOrderProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.patch.PatchOrderProtoResponse
import java.util.Date
import kotlin.random.Random
import org.bson.types.ObjectId

object OrderProtoFixture {

    fun createProtoRequest(request: CreateOrderRequest): CreateOrderProtoRequest = CreateOrderProtoRequest.newBuilder()
        .apply {
            orderBuilder.setCarId(request.carId)
            orderBuilder.setUserId(request.userId)
            orderBuilder.setFrom(Timestamp.newBuilder().setSeconds(request.from.time).build())
            orderBuilder.setTo(Timestamp.newBuilder().setSeconds(request.to.time).build())
        }
        .build()

    fun patchProtoRequest(id: String, request: UpdateOrderRequest): PatchOrderProtoRequest =
        PatchOrderProtoRequest.newBuilder()
            .apply {
                setId(id)
                patchBuilder.setFrom(Timestamp.newBuilder().setSeconds(request.from!!.time).build())
                patchBuilder.setTo(Timestamp.newBuilder().setSeconds(request.to!!.time).build())
            }
            .build()

    fun deleteProtoRequest(id: String): DeleteOrderProtoRequest = DeleteOrderProtoRequest
        .newBuilder()
        .setId(id)
        .build()

    fun createOrderResponse(request: CreateOrderRequest, price: Double) = OrderResponse(
        id = ObjectId().toString(),
        carId = request.carId,
        userId = request.userId,
        from = request.from,
        to = request.to,
        price = price.toBigDecimal(),
    )

    fun updateOrderResponse(request: UpdateOrderRequest, price: Double) = OrderResponse(
        id = ObjectId().toString(),
        carId = ObjectId().toString(),
        userId = ObjectId().toString(),
        from = request.from,
        to = request.to,
        price = price.toBigDecimal(),
    )

    fun findAllProtoResponse(): FindAllOrdersProtoResponse = FindAllOrdersProtoResponse
        .newBuilder()
        .apply { successBuilder.addOrders(randomAggregatedOrder()) }
        .build()

    fun aggregatedOrderDto(protoResponse: GetByIdOrderProtoResponse) = AggregatedOrderResponse(
        id = protoResponse.success.order.id,
        car = protoResponse.success.order.car.toResponse(),
        user = protoResponse.success.order.user.toResponse(),
        from = Date(protoResponse.success.order.from.seconds),
        to = Date(protoResponse.success.order.to.seconds),
        price = protoResponse.success.order.price.toBigDecimal(),
    )

    fun listOfAggregatedOrderDto(protoResponse: FindAllOrdersProtoResponse): List<AggregatedOrderResponse> =
        protoResponse.success.ordersList.map {
            AggregatedOrderResponse(
                id = it.id,
                car = it.car.toResponse(),
                user = it.user.toResponse(),
                from = Date(it.from.seconds),
                to = Date(it.to.seconds),
                price = it.price.toBigDecimal(),
            )
        }

    fun successfulUpdateProtoResponse(request: UpdateOrderRequest, price: Double): PatchOrderProtoResponse =
        PatchOrderProtoResponse.newBuilder()
            .apply {
                successBuilder.orderBuilder.setId(ObjectId().toString())
                successBuilder.orderBuilder.setCarId(ObjectId().toString())
                successBuilder.orderBuilder.setUserId(ObjectId().toString())
                successBuilder.orderBuilder.setFrom(Timestamp.newBuilder().setSeconds(request.from!!.time).build())
                successBuilder.orderBuilder.setTo(Timestamp.newBuilder().setSeconds(request.to!!.time).build())
                successBuilder.orderBuilder.setPrice(price)
            }
            .build()

    fun failureUpdateProtoResponse(): PatchOrderProtoResponse = PatchOrderProtoResponse
        .newBuilder()
        .apply { errorBuilder.setMessage("Order is not found").setExceptionTypeValue(1).build() }
        .build()

    fun successfulCreateProtoResponse(request: CreateOrderRequest, price: Double): CreateOrderProtoResponse =
        CreateOrderProtoResponse.newBuilder()
            .apply {
                successBuilder.orderBuilder.setId(ObjectId().toString())
                successBuilder.orderBuilder.setCarId(request.carId)
                successBuilder.orderBuilder.setUserId(request.userId)
                successBuilder.orderBuilder.setFrom(Timestamp.newBuilder().setSeconds(request.from.time).build())
                successBuilder.orderBuilder.setTo(Timestamp.newBuilder().setSeconds(request.to.time).build())
                successBuilder.orderBuilder.setPrice(price)
            }
            .build()

    fun failureCreateProtoResponse(): CreateOrderProtoResponse = CreateOrderProtoResponse
        .newBuilder()
        .apply { errorBuilder.setMessage("This car is already booked on these dates").setExceptionTypeValue(2).build() }
        .build()

    fun successfulGetByIdProtoResponse(): GetByIdOrderProtoResponse = GetByIdOrderProtoResponse
        .newBuilder()
        .apply { successBuilder.setOrder(randomAggregatedOrder()).build() }
        .build()

    fun failureGetByIdProtoResponse(): GetByIdOrderProtoResponse = GetByIdOrderProtoResponse
        .newBuilder()
        .apply { errorBuilder.setMessage("Order is not found").setExceptionTypeValue(1).build() }
        .build()

    private fun randomAggregatedOrder(): AggregatedOrder = AggregatedOrder.newBuilder()
        .setId(ObjectId().toString())
        .setCar(randomCar())
        .setUser(randomUser())
        .setPrice(Random.nextDouble(100.0, 500.0))
        .build()
}
