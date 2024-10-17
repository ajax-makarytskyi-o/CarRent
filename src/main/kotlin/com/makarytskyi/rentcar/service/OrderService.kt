package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.order.AggregatedOrderResponse
import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

internal interface OrderService {

    fun getById(id: String): Mono<AggregatedOrderResponse>

    fun findAll(page: Int, size: Int): Flux<AggregatedOrderResponse>

    fun create(createOrderRequest: CreateOrderRequest): Mono<OrderResponse>

    fun deleteById(id: String): Mono<Unit>

    fun findByUser(userId: String): Flux<OrderResponse>

    fun findByCar(carId: String): Flux<OrderResponse>

    fun findByCarAndUser(carId: String, userId: String): Flux<OrderResponse>

    fun patch(id: String, orderRequest: UpdateOrderRequest): Mono<OrderResponse>
}
