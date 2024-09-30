package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.order.AggregatedOrderResponse
import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest

internal interface OrderService {

    fun getById(id: String): AggregatedOrderResponse

    fun findAll(page: Int, size: Int): List<AggregatedOrderResponse>

    fun create(createOrderRequest: CreateOrderRequest): OrderResponse

    fun deleteById(id: String)

    fun findByUser(userId: String): List<OrderResponse>

    fun findByCar(carId: String): List<OrderResponse>

    fun findByCarAndUser(carId: String, userId: String): List<OrderResponse>

    fun patch(id: String, orderRequest: UpdateOrderRequest): OrderResponse
}
