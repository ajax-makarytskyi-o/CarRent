package com.makarytskyi.rentcar.fixtures.request

import com.makarytskyi.core.dto.order.AggregatedOrderResponse
import com.makarytskyi.core.dto.order.CreateOrderRequest
import com.makarytskyi.core.dto.order.OrderResponse
import com.makarytskyi.core.dto.order.UpdateOrderRequest
import com.makarytskyi.internalapi.reqreply.create.CreateOrderProtoRequest
import com.makarytskyi.internalapi.reqreply.create.CreateOrderProtoResponse
import com.makarytskyi.rentcar.mapper.toProto
import com.makarytskyi.rentcar.proto.reqreply.delete.DeleteOrderProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.find_all.FindAllOrdersProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.find_by_id.GetByIdOrderProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.find_by_id.GetByIdOrderProtoResponse
import com.makarytskyi.rentcar.proto.reqreply.patch.PatchOrderProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.patch.PatchOrderProtoResponse
import com.makarytskyi.rentcar.util.dateToTimestamp

object OrderProtoFixtures {
    fun createOrderProtoRequest(request: CreateOrderRequest): CreateOrderProtoRequest =
        CreateOrderProtoRequest.newBuilder()
            .apply {
                orderBuilder.setCarId(request.carId)
                orderBuilder.setUserId(request.userId)
                orderBuilder.setFrom(dateToTimestamp(request.from))
                orderBuilder.setTo(dateToTimestamp(request.to))
            }
            .build()

    fun successCreateProtoResponse(response: OrderResponse): CreateOrderProtoResponse = CreateOrderProtoResponse
        .newBuilder()
        .apply {
            successBuilder.orderBuilder.setId(response.id)
            successBuilder.orderBuilder.setCarId(response.carId)
            successBuilder.orderBuilder.setUserId(response.userId)
            successBuilder.orderBuilder.setFrom(dateToTimestamp(response.from!!))
            successBuilder.orderBuilder.setTo(dateToTimestamp(response.to!!))
            successBuilder.orderBuilder.setPrice(response.price!!.toDouble())
        }
        .build()

    fun failCreateProtoResponse(exception: Exception): CreateOrderProtoResponse = CreateOrderProtoResponse.newBuilder()
        .apply { errorBuilder.setMessage(exception.message).setExceptionType(exception.toProto()) }
        .build()

    fun successPatchProtoResponse(response: OrderResponse): PatchOrderProtoResponse = PatchOrderProtoResponse
        .newBuilder()
        .apply {
            successBuilder.orderBuilder.setId(response.id)
            successBuilder.orderBuilder.setCarId(response.carId)
            successBuilder.orderBuilder.setUserId(response.userId)
            successBuilder.orderBuilder.setFrom(dateToTimestamp(response.from!!))
            successBuilder.orderBuilder.setTo(dateToTimestamp(response.to!!))
            successBuilder.orderBuilder.setPrice(response.price!!.toDouble())
        }
        .build()

    fun failPatchProtoResponse(exception: Exception): PatchOrderProtoResponse = PatchOrderProtoResponse.newBuilder()
        .apply { errorBuilder.setMessage(exception.message).setExceptionType(exception.toProto()) }
        .build()

    fun updateOrderProtoRequest(id: String, request: UpdateOrderRequest): PatchOrderProtoRequest =
        PatchOrderProtoRequest.newBuilder()
            .apply {
                setId(id)
                patchBuilder.setFrom(dateToTimestamp(request.from!!))
                patchBuilder.setTo(dateToTimestamp(request.to!!))
            }
            .build()

    fun getByIdOrderProtoRequest(id: String): GetByIdOrderProtoRequest = GetByIdOrderProtoRequest
        .newBuilder()
        .setId(id)
        .build()

    fun successGetByIdProtoResponse(response: AggregatedOrderResponse): GetByIdOrderProtoResponse =
        GetByIdOrderProtoResponse
            .newBuilder()
            .apply {
                successBuilder.orderBuilder.setId(response.id)
                successBuilder.orderBuilder.setCar(response.car.toProto())
                successBuilder.orderBuilder.setUser(response.user.toProto())
                successBuilder.orderBuilder.setFrom(dateToTimestamp(response.from!!))
                successBuilder.orderBuilder.setTo(dateToTimestamp(response.to!!))
                successBuilder.orderBuilder.setPrice(response.price!!.toDouble())
            }
            .build()

    fun failGetByIdProtoResponse(exception: Exception): GetByIdOrderProtoResponse =
        GetByIdOrderProtoResponse.newBuilder()
            .apply { errorBuilder.setMessage(exception.message).setExceptionType(exception.toProto()) }
            .build()

    fun deleteOrderProtoRequest(id: String): DeleteOrderProtoRequest =
        DeleteOrderProtoRequest.newBuilder()
            .setId(id)
            .build()

    fun findAllOrderProtoRequest(page: Int, size: Int): FindAllOrdersProtoRequest = FindAllOrdersProtoRequest
        .newBuilder()
        .setPage(page)
        .setSize(size)
        .build()
}
