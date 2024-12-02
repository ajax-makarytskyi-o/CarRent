package com.makarytskyi.rentcar.service.impl

import com.makarytskyi.core.dto.order.AggregatedOrderResponseDto
import com.makarytskyi.core.dto.order.CreateOrderRequestDto
import com.makarytskyi.core.dto.order.OrderResponseDto
import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.rentcar.annotation.InvocationTracker
import com.makarytskyi.rentcar.kafka.CreateOrderKafkaProducer
import com.makarytskyi.rentcar.mapper.OrderMapper.toEntity
import com.makarytskyi.rentcar.mapper.OrderMapper.toPatch
import com.makarytskyi.rentcar.mapper.OrderMapper.toProto
import com.makarytskyi.rentcar.mapper.OrderMapper.toResponse
import com.makarytskyi.rentcar.model.MongoOrder
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.service.OrderService
import java.math.BigDecimal
import java.util.Date
import org.slf4j.LoggerFactory
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
    private val createOrderKafkaProducer: CreateOrderKafkaProducer,
) : OrderService {

    override fun getById(id: String): Mono<AggregatedOrderResponseDto> =
        orderRepository.findFullById(id)
            .switchIfEmpty { Mono.error(NotFoundException("Order with id $id is not found")) }
            .map { it.toResponse() }

    override fun findAll(page: Int, size: Int): Flux<AggregatedOrderResponseDto> =
        orderRepository.findFullAll(page, size).map { it.toResponse() }

    override fun create(createOrderRequest: CreateOrderRequestDto): Mono<OrderResponseDto> {
        return createOrderRequest.toMono()
            .doOnNext { validateDates(it.from, it.to) }
            .flatMap {
                Mono.zip(
                    validateUserExists(createOrderRequest.userId)
                        .subscribeOn(Schedulers.boundedElastic()),
                    validateCarAvailability(
                        createOrderRequest.carId,
                        createOrderRequest.from,
                        createOrderRequest.to
                    ).subscribeOn(Schedulers.boundedElastic())
                ).thenReturn(createOrderRequest)
            }
            .flatMap { orderRepository.create(createOrderRequest.toEntity()) }
            .flatMap { order -> getCarPrice(createOrderRequest.carId).map { order.toResponse(it) } }
            .doOnNext {
                createOrderKafkaProducer.sendCreateOrder(it.toProto())
                    .doOnError { e ->
                        log.atError()
                            .setMessage("Error happened during sending message to Kafka: {}")
                            .addArgument(e.message)
                            .setCause(e)
                    }
                    .subscribe()
            }
    }

    override fun deleteById(id: String): Mono<Unit> = orderRepository.deleteById(id)

    override fun findByUser(userId: String): Flux<OrderResponseDto> = orderRepository.findByUserId(userId)
        .flatMap { it.toMono().zipWith(getCarPrice(it.carId.toString())) }
        .map { (order, carPrice) -> order.toResponse(carPrice) }

    override fun findByCar(carId: String): Flux<OrderResponseDto> = orderRepository.findByCarId(carId)
        .flatMap { it.toMono().zipWith(getCarPrice(it.carId.toString())) }
        .map { (order, carPrice) -> order.toResponse(carPrice) }

    override fun findByCarAndUser(carId: String, userId: String): Flux<OrderResponseDto> =
        orderRepository.findByCarIdAndUserId(carId, userId)
            .flatMap { it.toMono().zipWith(getCarPrice(it.carId.toString())) }
            .map { (order, carPrice) -> order.toResponse(carPrice) }

    override fun patch(id: String, orderRequest: UpdateOrderRequestDto): Mono<OrderResponseDto> =
        orderRepository.findFullById(id)
            .switchIfEmpty { Mono.error(NotFoundException("Order with id $id is not found")) }
            .flatMap { order ->
                val from = orderRequest.from ?: order.from
                val to = orderRequest.to ?: order.to
                validateDates(from, to)
                order.car?.let {
                    validateCarAvailability(it.id.toString(), from, to).then(orderRequest.toMono())
                } ?: IllegalArgumentException("Car id should not be null").toMono()
            }
            .flatMap { orderRepository.patch(id, it.toPatch()) }
            .flatMap { order -> getCarPrice(order.carId.toString()).map { order.toResponse(it) } }


    override fun findOrderByCarAndDate(carId: String, date: Date): Mono<OrderResponseDto> =
        Mono.zip(
            orderRepository.findOrderByDateAndCarId(date, carId),
            getCarPrice(carId),
        )
            .map { (order, carPrice) -> order.toResponse(carPrice) }

    private fun validateDates(from: Date?, to: Date?) {
        require(to?.after(from) == true) { "Start date must be before end date" }
        require(from?.after(Date()) == true) { "Dates must be in future" }
    }

    private fun validateUserExists(userId: String) = userRepository.findById(userId)
        .switchIfEmpty { Mono.error(NotFoundException("User with id $userId is not found")) }

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

    companion object {
        val log = LoggerFactory.getLogger(OrderServiceImpl::class.java)
    }
}
