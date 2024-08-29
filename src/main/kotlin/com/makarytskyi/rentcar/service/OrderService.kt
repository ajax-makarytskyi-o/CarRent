package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.model.Order
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import java.util.Date
import org.springframework.stereotype.Service

@Service
internal class OrderService(
    private val orderRepository: OrderRepository,
    private val carRepository: CarRepository,
    private val userRepository: UserRepository
) {

    fun getById(id: String): OrderResponse =
        orderRepository.findById(id)?.let { Order.toResponse(it, getCarPrice(it.carId)) }
            ?: throw ResourceNotFoundException("Order with id $id is not found")

    fun findAll(): List<OrderResponse> =
        orderRepository.findAll().map { Order.toResponse(it, getCarPrice(it.carId)) }
            .toList()

    fun create(createOrderRequest: CreateOrderRequest): OrderResponse {

        validateDates(createOrderRequest.from, createOrderRequest.to)
        validateUserExists(createOrderRequest.userId)
        validateCarAvailability(createOrderRequest.carId, createOrderRequest.from, createOrderRequest.to)

        return Order.toResponse(
            orderRepository.create(CreateOrderRequest.toEntity(createOrderRequest)),
            getCarPrice(createOrderRequest.carId)
        )
    }

    fun deleteById(id: String) = orderRepository.deleteById(id)

    fun findByUser(userId: String): List<OrderResponse> = orderRepository.findByUserId(userId).map {
        Order.toResponse(it, getCarPrice(it.carId))
    }

    fun findByCar(carId: String): List<OrderResponse> = orderRepository.findByCarId(carId).map {
        Order.toResponse(it, getCarPrice(it.carId))
    }

    fun findByCarAndUser(carId: String, userId: String): List<OrderResponse> =
        orderRepository.findByUserIdAndCarId(carId, userId)
            .map { Order.toResponse(it, getCarPrice(it.carId)) }

    fun update(id: String, orderRequest: UpdateOrderRequest): OrderResponse {
        val order = orderRepository.findById(id) ?: throw ResourceNotFoundException("Order with $id is not found")
        val newFrom = orderRequest.from ?: order.from
        val newTo = orderRequest.to ?: order.to
        validateDates(newFrom!!, newTo!!)
        validateCarAvailability(order.carId, newFrom, newTo)

        return orderRepository.update(id, UpdateOrderRequest.toEntity(orderRequest))
            ?.let { Order.toResponse(it, getCarPrice(order.carId)) }
            ?: throw ResourceNotFoundException("Order with $id is not found")
    }

    private fun validateDates(from: Date, to: Date) {
        require(to.after(from)) { "Start date must be before end date"}
        require(from.after(Date()))
    }

    private fun validateUserExists(userId: String) {
        require(userRepository.findById(userId) != null) { "User with id $userId is not found" }
    }

    private fun validateCarAvailability(carId: String?, from: Date, to: Date) {
        require(carId != null) { "Car id should not be null" }
        carRepository.findById(carId) ?: throw ResourceNotFoundException("Car with id $carId is not found")

        val carOrders = orderRepository.findByCarId(carId)

        require(carOrders.none { it.from?.before(to) == true && it.to?.after(from) == true })
        { "Order on these dates is already exist" }
    }

    private fun getCarPrice(carId: String?): Int? = carId?.let { carRepository.findById(it)?.price }
}
