package com.makarytskyi.gateway.application.input

import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderInputPort {
    fun getFullById(request: GetByIdOrderRequest): Mono<GetByIdOrderResponse>

    fun create(request: CreateOrderRequest): Mono<CreateOrderResponse>

    fun streamCreatedOrdersByUserId(userId: String): Flux<Order>
}
