package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
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
    private val orderRepository: OrderRepository,
    private val carRepository: CarRepository,
    private val userRepository: UserRepository
) {

    fun findById(id: String): OrderResponse =
        orderRepository.findById(id)
            ?.let { order -> order.toResponse(order.carId?.let { carRepository.findById(it)?.price }) }
            ?: throw OrderNotFoundException("Order with id $id is not found")

    fun findAll(): List<OrderResponse> =
        orderRepository.findAll().map { it.toResponse(it.carId?.let { carId -> carRepository.findById(carId)?.price }) }
            .toList()

    fun save(createOrderRequest: CreateOrderRequest): OrderResponse {
        if (!createOrderRequest.to.after(createOrderRequest.from))
            throw IllegalArgumentException("Start date must be before end date")

        userRepository.findById(createOrderRequest.userId)
            ?: throw IllegalArgumentException("User with id ${createOrderRequest.userId} is not found")

        carRepository.findById(createOrderRequest.carId)
            ?: throw IllegalArgumentException("Car with id ${createOrderRequest.carId} is not found")

        val carOrders: List<Order> = orderRepository.findByCarId(createOrderRequest.carId)

        if (carOrders.any { it.from?.before(createOrderRequest.to) == true && it.to?.after(createOrderRequest.from) == true })
            throw IllegalArgumentException("Order on these dates is already exist")

        val carPrice = carRepository.findById(createOrderRequest.carId)?.price

        return orderRepository.save(createOrderRequest.toEntity()).toResponse(carPrice)
    }

    fun deleteById(id: String) = orderRepository.findById(id)?.let { orderRepository.deleteById(id) }
        ?: throw OrderNotFoundException("Order with id $id is not found")


    fun findByUserId(userId: String): List<OrderResponse> = orderRepository.findByUserId(userId).map {
        it.toResponse(it.carId?.let { carId -> carRepository.findById(carId)?.price })
    }

    fun findByCarId(carId: String): List<OrderResponse> = orderRepository.findByCarId(carId).map {
        it.toResponse(it.carId?.let { carId -> carRepository.findById(carId)?.price })
    }

    fun update(id: String, orderRequest: UpdateOrderRequest): OrderResponse {
        if (!orderRequest.to.after(orderRequest.from))
            throw IllegalArgumentException("Start date must be before end date")

        val order: Order = orderRepository.findById(id) ?: throw OrderNotFoundException("Order with $id is not found")

        val carOrders: List<Order> = order.carId?.let { orderRepository.findByCarId(it) }
            ?: throw IllegalArgumentException("Car in order must be set")

        if (carOrders.any { it.from?.before(orderRequest.to) == true && it.to?.after(orderRequest.from) == true })
            throw IllegalArgumentException("Order on these dates is already exist")

        val carPrice = carRepository.findById(order.carId)?.price

        return orderRepository.updateDates(id, orderRequest.from, orderRequest.to)?.toResponse(carPrice)
            ?: throw OrderNotFoundException("Order with $id is not found")
    }
}
