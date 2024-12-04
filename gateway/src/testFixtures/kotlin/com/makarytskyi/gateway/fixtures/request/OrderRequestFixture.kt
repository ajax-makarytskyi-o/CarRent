package com.makarytskyi.gateway.fixtures.request

import com.makarytskyi.core.dto.order.CreateOrderRequestDto
import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import com.makarytskyi.gateway.fixtures.Utils.getDateFromNow
import com.makarytskyi.gateway.infrastructure.util.Util.dateToTimestamp
import com.makarytskyi.grpcapi.input.reqreply.order.CreateOrderRequest
import org.bson.types.ObjectId

object OrderRequestFixture {
    val tomorrow = getDateFromNow(1)
    val twoDaysAfter = getDateFromNow(2)
    val yesterday = getDateFromNow(-1)

    fun randomCreateRequest() = CreateOrderRequestDto(
        carId = ObjectId().toString(),
        userId = ObjectId().toString(),
        from = tomorrow,
        to = twoDaysAfter,
    )

    fun grpcCreateRequest(request: CreateOrderRequestDto): CreateOrderRequest = CreateOrderRequest
        .newBuilder().also {
            it.orderBuilder.also { order ->
                order.from = dateToTimestamp(request.from)
                order.to = dateToTimestamp(request.to)
                order.carId = request.carId
                order.userId = request.userId
            }
        }
        .build()

    fun randomUpdateRequest() = UpdateOrderRequestDto(
        from = tomorrow,
        to = twoDaysAfter,
    )
}
