package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import org.springframework.stereotype.Service

@Service
internal interface OrderService {

    fun getById(id: String): OrderResponse

    fun findAll(): List<OrderResponse>

    fun create(createOrderRequest: CreateOrderRequest): OrderResponse

    fun deleteById(id: String)

    fun findByUser(userId: String): List<OrderResponse>

    fun findByCar(carId: String): List<OrderResponse>

    fun findByCarAndUser(carId: String, userId: String): List<OrderResponse>

    fun update(id: String, orderRequest: UpdateOrderRequest): OrderResponse
}
