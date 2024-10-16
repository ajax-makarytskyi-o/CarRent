package com.makarytskyi.rentcar.service.impl

import com.makarytskyi.rentcar.annotation.InvocationTracker
import com.makarytskyi.rentcar.dto.order.AggregatedOrderResponse
import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import com.makarytskyi.rentcar.exception.NotFoundException
import com.makarytskyi.rentcar.model.MongoOrder
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.service.OrderService
import java.math.BigDecimal
import java.util.Date
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@InvocationTracker
@Service
internal class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val carRepository: CarRepository,
    private val userRepository: UserRepository,
) : OrderService {

    override fun getById(id: String): Mono<AggregatedOrderResponse> =
        orderRepository.findFullById(id)
            .switchIfEmpty { Mono.error(NotFoundException("Order with id $id is not found")) }
            .map { AggregatedOrderResponse.from(it) }

    override fun findAll(page: Int, size: Int): Flux<AggregatedOrderResponse> =
        orderRepository.findFullAll(page, size).map { AggregatedOrderResponse.from(it) }

    override fun create(createOrderRequest: CreateOrderRequest): Mono<OrderResponse> {
        return createOrderRequest.toMono()
            .flatMap {
                validateDates(it.from, it.to)
                validateUserExists(it.userId)
            }
            .flatMap {
                validateCarAvailability(
                    createOrderRequest.carId,
                    createOrderRequest.from,
                    createOrderRequest.to
                ).thenReturn(it)
            }
            .flatMap { orderRepository.create(CreateOrderRequest.toEntity(createOrderRequest)) }
            .flatMap { order -> getCarPrice(createOrderRequest.carId).map { OrderResponse.from(order, it) } }
    }

    override fun deleteById(id: String): Mono<Unit> = orderRepository.deleteById(id)

    override fun findByUser(userId: String): Flux<OrderResponse> = orderRepository.findByUserId(userId)
        .flatMap { Mono.just(it).zipWith(getCarPrice(it.carId.toString())) }
        .map { OrderResponse.from(it.t1, it.t2) }

    override fun findByCar(carId: String): Flux<OrderResponse> = orderRepository.findByCarId(carId)
        .flatMap { Mono.just(it).zipWith(getCarPrice(it.carId.toString())) }
        .map { OrderResponse.from(it.t1, it.t2) }

    override fun findByCarAndUser(carId: String, userId: String): Flux<OrderResponse> =
        orderRepository.findByCarIdAndUserId(carId, userId)
            .flatMap { Mono.just(it).zipWith(getCarPrice(it.carId.toString())) }
            .map { OrderResponse.from(it.t1, it.t2) }

    override fun patch(id: String, orderRequest: UpdateOrderRequest): Mono<OrderResponse> =
        orderRepository.findFullById(id)
            .switchIfEmpty { Mono.error(NotFoundException("Order with $id is not found")) }
            .flatMap {
                val from = orderRequest.from ?: it.from
                val to = orderRequest.to ?: it.to
                validateDates(from, to)
                validateCarAvailability(it.car?.id.toString(), from, to)
            }
            .switchIfEmpty { orderRepository.patch(id, UpdateOrderRequest.toPatch(orderRequest)) }
            .flatMap { order -> getCarPrice(order.carId.toString()).map { OrderResponse.from(order, it) } }

    private fun validateDates(from: Date?, to: Date?) {
        requireNotNull(from) { "Start date must be not null" }
        requireNotNull(to) { "End date must be not null" }
        require(to.after(from)) { "Start date must be before end date" }
        require(from.after(Date())) { "Dates must be in future" }
    }

    private fun validateUserExists(userId: String) = userRepository.findById(userId)
        .switchIfEmpty { Mono.error(IllegalArgumentException("User with id $userId is not found")) }

    private fun validateCarAvailability(carId: String?, from: Date?, to: Date?): Mono<MongoOrder> {
        return Mono.justOrEmpty(carId)
            .switchIfEmpty { Mono.error(IllegalArgumentException("Car id should not be null")) }
            .flatMap { carRepository.findById(it) }
            .switchIfEmpty { Mono.error(NotFoundException("Car with id $carId is not found")) }
            .flatMapMany { orderRepository.findByCarId(it.id.toString()) }
            .filter { it.from?.before(to) == true && it.to?.after(from) == true }
            .next()
            .flatMap { Mono.error(IllegalArgumentException("Order on these dates is already exist")) }
    }

    private fun getCarPrice(carId: String?): Mono<BigDecimal> =
        Mono.justOrEmpty(carId)
            .flatMap { carRepository.findById(it) }
            .map { it.price ?: BigDecimal.ZERO }
            .defaultIfEmpty(BigDecimal.ZERO)
}
