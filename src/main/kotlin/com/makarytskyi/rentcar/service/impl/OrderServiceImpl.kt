package com.makarytskyi.rentcar.service.impl

import com.makarytskyi.rentcar.annotation.InvocationTracker
import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.service.OrderService
import java.util.Date
import org.springframework.stereotype.Service

@InvocationTracker
@Service
internal class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val carRepository: CarRepository,
    private val userRepository: UserRepository
) : OrderService {

    override fun getById(id: String): OrderResponse =
        orderRepository.findById(id)?.let { OrderResponse.from(it, getCarPrice(it.carId)) }
            ?: throw ResourceNotFoundException("Order with id $id is not found")

    override fun findAll(): List<OrderResponse> =
        orderRepository.findAll().map { OrderResponse.from(it, getCarPrice(it.carId)) }
            .toList()

    override fun create(createOrderRequest: CreateOrderRequest): OrderResponse {

        validateDates(createOrderRequest.from, createOrderRequest.to)
        validateUserExists(createOrderRequest.userId)
        validateCarAvailability(createOrderRequest.carId, createOrderRequest.from, createOrderRequest.to)

        return OrderResponse.from(
            orderRepository.create(CreateOrderRequest.toEntity(createOrderRequest)),
            getCarPrice(createOrderRequest.carId)
        )
    }

    override fun deleteById(id: String) = orderRepository.deleteById(id)

    override fun findByUser(userId: String): List<OrderResponse> = orderRepository.findByUserId(userId).map {
        OrderResponse.from(it, getCarPrice(it.carId))
    }

    override fun findByCar(carId: String): List<OrderResponse> = orderRepository.findByCarId(carId).map {
        OrderResponse.from(it, getCarPrice(it.carId))
    }

    override fun findByCarAndUser(carId: String, userId: String): List<OrderResponse> =
        orderRepository.findByUserIdAndCarId(carId, userId)
            .map { OrderResponse.from(it, getCarPrice(it.carId)) }

    override fun update(id: String, orderRequest: UpdateOrderRequest): OrderResponse {
        val order = orderRepository.findById(id) ?: throw ResourceNotFoundException("Order with $id is not found")
        val newFrom = orderRequest.from ?: order.from
        val newTo = orderRequest.to ?: order.to
        validateDates(newFrom!!, newTo!!)
        validateCarAvailability(order.carId, newFrom, newTo)

        return orderRepository.update(id, UpdateOrderRequest.toEntity(orderRequest))
            ?.let { OrderResponse.from(it, getCarPrice(order.carId)) }
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
