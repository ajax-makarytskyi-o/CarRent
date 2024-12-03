package com.makarytskyi.rentcar.order.application.service

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.rentcar.car.application.port.output.CarOutputPort
import com.makarytskyi.rentcar.common.annotation.InvocationTracker
import com.makarytskyi.rentcar.order.application.mapper.toProto
import com.makarytskyi.rentcar.order.application.mapper.toResponse
import com.makarytskyi.rentcar.order.application.port.input.OrderInputPort
import com.makarytskyi.rentcar.order.application.port.output.CreateOrderProducerOutputPort
import com.makarytskyi.rentcar.order.application.port.output.OrderMongoOutputPort
import com.makarytskyi.rentcar.order.domain.DomainOrder
import com.makarytskyi.rentcar.order.domain.patch.DomainOrderPatch
import com.makarytskyi.rentcar.order.domain.projection.AggregatedDomainOrder
import com.makarytskyi.rentcar.user.application.port.output.UserOutputPort
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
class OrderService(
    private val orderMongoOutputPort: OrderMongoOutputPort,
    private val carOutputPort: CarOutputPort,
    private val userOutputPort: UserOutputPort,
    private val createOrderKafkaProducer: CreateOrderProducerOutputPort,
) : OrderInputPort {
    override fun getById(id: String): Mono<AggregatedDomainOrder> =
        orderMongoOutputPort.findFullById(id)
            .switchIfEmpty { Mono.error(NotFoundException("Order with id $id is not found")) }
            .map { it.toResponse() }

    override fun findAll(page: Int, size: Int): Flux<AggregatedDomainOrder> =
        orderMongoOutputPort.findFullAll(page, size)

    override fun create(createOrderRequest: DomainOrder): Mono<DomainOrder> {
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
            .flatMap {
                Mono.zip(
                    orderMongoOutputPort.create(createOrderRequest),
                    getCarPrice(it.carId)
                ).map { (order, carPrice) -> order.copy(price = order.totalPrice(carPrice)) }
            }
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

    override fun deleteById(id: String): Mono<Unit> = orderMongoOutputPort.deleteById(id)

    override fun findByUser(userId: String): Flux<DomainOrder> = orderMongoOutputPort.findByUserId(userId)

    override fun findByCar(carId: String): Flux<DomainOrder> = orderMongoOutputPort.findByCarId(carId)

    override fun findByCarAndUser(carId: String, userId: String): Flux<DomainOrder> =
        orderMongoOutputPort.findByCarIdAndUserId(carId, userId)

    override fun patch(id: String, orderRequest: DomainOrderPatch): Mono<DomainOrder> =
        orderMongoOutputPort.findFullById(id)
            .switchIfEmpty { Mono.error(NotFoundException("Order with id $id is not found")) }
            .flatMap { order ->
                val from = orderRequest.from ?: order.from
                val to = orderRequest.to ?: order.to
                validateDates(from, to)
                validateCarAvailability(order.car.id.toString(), from, to).then(orderRequest.toMono())
            }
            .flatMap { patch ->
                orderMongoOutputPort.findById(id)
                    .flatMap {
                        Mono.zip(
                            orderMongoOutputPort.patch(id, it.fromPatch(patch)),
                            getCarPrice(it.carId)
                        ).map { (order, carPrice) -> order.copy(price = order.totalPrice(carPrice)) }
                    }
            }

    override fun findOrderByCarAndDate(carId: String, date: Date): Mono<DomainOrder> =
        Mono.zip(
            orderMongoOutputPort.findOrderByDateAndCarId(date, carId),
            getCarPrice(carId),
        )
            .map { (order, price) -> order.toResponse(price) }

    private fun validateDates(from: Date?, to: Date?) {
        require(to?.after(from) == true) { "Start date must be before end date" }
        require(from?.after(Date()) == true) { "Dates must be in future" }
    }

    private fun validateUserExists(userId: String) = userOutputPort.findById(userId)
        .switchIfEmpty { Mono.error(NotFoundException("User with id $userId is not found")) }

    private fun validateCarAvailability(carId: String, from: Date?, to: Date?): Mono<DomainOrder> {
        return carOutputPort.findById(carId)
            .switchIfEmpty { Mono.error(NotFoundException("Car with id $carId is not found")) }
            .flatMapMany { orderMongoOutputPort.findByCarId(it.id.toString()) }
            .filter { it.from.before(to) && it.to.after(from) }
            .flatMap<DomainOrder> { Mono.error(IllegalArgumentException("Car is already booked on this time")) }
            .toMono()
    }

    private fun getCarPrice(carId: String): Mono<BigDecimal> =
        carOutputPort.findById(carId)
            .map { it.price }
            .defaultIfEmpty(BigDecimal.ZERO)

    companion object {
        val log = LoggerFactory.getLogger(OrderService::class.java)
    }
}
