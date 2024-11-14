package com.makarytskyi.rentcar.service

import com.makarytskyi.core.dto.order.AggregatedOrderResponseDto
import com.makarytskyi.core.dto.order.CreateOrderRequestDto
import com.makarytskyi.core.dto.order.OrderResponseDto
import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import java.util.Date
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderService {

    fun getById(id: String): Mono<AggregatedOrderResponseDto>

    fun findAll(page: Int, size: Int): Flux<AggregatedOrderResponseDto>

    fun create(createOrderRequest: CreateOrderRequestDto): Mono<OrderResponseDto>

    fun deleteById(id: String): Mono<Unit>

    fun findByUser(userId: String): Flux<OrderResponseDto>

    fun findByCar(carId: String): Flux<OrderResponseDto>

    fun findByCarAndUser(carId: String, userId: String): Flux<OrderResponseDto>

    fun patch(id: String, orderRequest: UpdateOrderRequestDto): Mono<OrderResponseDto>

    fun findOrderByCarAndDate(carId: String, date: Date): Mono<OrderResponseDto>
}
