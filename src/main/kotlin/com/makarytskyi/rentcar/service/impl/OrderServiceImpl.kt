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
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

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
            .doOnNext { validateDates(it.from, it.to) }
            .flatMap {
                Mono.zip(
                    Mono.defer { validateUserExists(createOrderRequest.userId) }
                        .subscribeOn(Schedulers.boundedElastic()),
                    Mono.defer {
                        validateCarAvailability(
                            createOrderRequest.carId,
                            createOrderRequest.from,
                            createOrderRequest.to
                        )
                    }.subscribeOn(Schedulers.boundedElastic())
                ).thenReturn(Unit)
            }
            .flatMap { orderRepository.create(CreateOrderRequest.toEntity(createOrderRequest)) }
            .flatMap { order -> getCarPrice(createOrderRequest.carId).map { OrderResponse.from(order, it) } }
    }

    override fun deleteById(id: String): Mono<Unit> = orderRepository.deleteById(id)

    override fun findByUser(userId: String): Flux<OrderResponse> = orderRepository.findByUserId(userId)
        .flatMap { Mono.just(it).zipWith(getCarPrice(it.carId.toString())) }
        .map { (order, carPrice) -> OrderResponse.from(order, carPrice) }

    override fun findByCar(carId: String): Flux<OrderResponse> = orderRepository.findByCarId(carId)
        .flatMap { Mono.just(it).zipWith(getCarPrice(it.carId.toString())) }
        .map { (order, carPrice) -> OrderResponse.from(order, carPrice) }

    override fun findByCarAndUser(carId: String, userId: String): Flux<OrderResponse> =
        orderRepository.findByCarIdAndUserId(carId, userId)
            .flatMap { Mono.just(it).zipWith(getCarPrice(it.carId.toString())) }
            .map { (order, carPrice) -> OrderResponse.from(order, carPrice) }

    override fun patch(id: String, orderRequest: UpdateOrderRequest): Mono<OrderResponse> =
        orderRepository.findFullById(id)
            .switchIfEmpty { Mono.error(NotFoundException("Order with $id is not found")) }
            .flatMap {
                val from = orderRequest.from ?: it.from
                val to = orderRequest.to ?: it.to
                validateDates(from, to)
                it.car?.let {
                    validateCarAvailability(it.id.toString(), from, to).then(orderRequest.toMono())
                } ?: IllegalArgumentException("Car id should not be null").toMono()
            }
            .flatMap { orderRepository.patch(id, UpdateOrderRequest.toPatch(it)) }
            .flatMap { order -> getCarPrice(order.carId.toString()).map { OrderResponse.from(order, it) } }

    private fun validateDates(from: Date?, to: Date?) {
        require(to?.after(from) == true) { "Start date must be before end date" }
        require(from?.after(Date()) == true) { "Dates must be in future" }
    }

    private fun validateUserExists(userId: String) = userRepository.findById(userId)
        .switchIfEmpty { Mono.error(IllegalArgumentException("User with id $userId is not found")) }

    private fun validateCarAvailability(carId: String, from: Date?, to: Date?): Mono<MongoOrder> {
        return carRepository.findById(carId)
            .switchIfEmpty { Mono.error(NotFoundException("Car with id $carId is not found")) }
            .flatMapMany { orderRepository.findByCarId(it.id.toString()) }
            .filter { it.from?.before(to) == true && it.to?.after(from) == true }
            .flatMap<MongoOrder> { Mono.error(IllegalArgumentException("Car is already booked on this time")) }
            .toMono()
    }

    private fun getCarPrice(carId: String): Mono<BigDecimal> =
        carRepository.findById(carId)
            .map { it.price ?: BigDecimal.ZERO }
            .defaultIfEmpty(BigDecimal.ZERO)
}
