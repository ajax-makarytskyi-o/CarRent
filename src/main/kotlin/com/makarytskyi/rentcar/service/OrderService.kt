package com.makarytskyi.rentcar.service.impl

import com.makarytskyi.rentcar.dto.order.OrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import com.makarytskyi.rentcar.exception.OrderNotFoundException
import com.makarytskyi.rentcar.model.Order
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class OrderService(
    val orderRepository: OrderRepository,
    val carRepository: CarRepository,
    val userRepository: UserRepository
) {

    fun findById(id: String?): OrderResponse {
        val order = orderRepository.findById(id)
            ?: throw OrderNotFoundException("Order with id $id is not found")

        return order.toResponse(carRepository.findById(order.carId)?.price)
    }

    fun findAll(): List<OrderResponse> {
        val orders = orderRepository.findAll()
        return orders.map { it.toResponse(carRepository.findById(it.carId)?.price) }.toList()
    }

    fun save(orderRequest: OrderRequest): OrderResponse {
        val order: Order = orderRequest.toEntity()

        if (userRepository.findById(order.userId) == null)
            throw IllegalArgumentException("User with id ${order.userId} is not found")

        if (carRepository.findById(order.carId) == null)
            throw IllegalArgumentException("Car with id ${order.carId} is not found")

        val carOrders: List<Order> = order.carId?.let { orderRepository.findByCarId(it) }
            ?: throw IllegalArgumentException("Car in order must be set")

        if (carOrders.any { it.from?.before(it.to) == true && it.to?.after(it.from) == true })
            throw IllegalArgumentException("Order on these dates is already exist")

        val carPrice = carRepository.findById(order.carId)?.price ?: 0

        return orderRepository.save(order).toResponse(carPrice)
    }

    fun deleteById(id: String) {
        if (orderRepository.findById(id) != null)
            orderRepository.deleteById(id)
        else
            throw OrderNotFoundException("Order with id $id is not found")
    }

    fun findByUserId(userId: String): List<OrderResponse> = orderRepository.findByUserId(userId).map { it.toResponse(carRepository.findById(it.carId)?.price) }

    fun findByCarId(carId: String): List<OrderResponse> = orderRepository.findByCarId(carId).map { it.toResponse(carRepository.findById(it.carId)?.price) }

    fun update(id: String, orderRequest: UpdateOrderRequest): OrderResponse {
        if (orderRequest.to?.after(orderRequest.from) == false)
            throw IllegalArgumentException("Dates must be non-null. Start date must be before end date")

        val order: Order = orderRepository.findById(id) ?: throw OrderNotFoundException("Order with $id is not found")

        val carOrders: List<Order> = order.carId?.let { orderRepository.findByCarId(it) }
            ?: throw IllegalArgumentException("Car in order must be set")

        if (carOrders.any { it.from?.before(orderRequest.to) == true && it.to?.after(orderRequest.from) == true })
            throw IllegalArgumentException("Order on these dates is already exist")

        val carPrice = carRepository.findById(order.carId)?.price ?: 0

        return orderRepository.updateDates(id, orderRequest.from, orderRequest.to)?.toResponse(carPrice) ?: throw OrderNotFoundException("Order with $id is not found")
    }
}
