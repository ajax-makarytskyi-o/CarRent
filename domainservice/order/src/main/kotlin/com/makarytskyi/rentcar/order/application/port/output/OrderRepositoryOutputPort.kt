package com.makarytskyi.rentcar.order.application.port.output

import com.makarytskyi.rentcar.order.domain.DomainOrder
import com.makarytskyi.rentcar.order.domain.create.CreateOrder
import com.makarytskyi.rentcar.order.domain.projection.AggregatedDomainOrder
import java.util.Date
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderRepositoryOutputPort {
    fun findFullById(id: String): Mono<AggregatedDomainOrder>

    fun findById(id: String): Mono<DomainOrder>

    fun findFullAll(page: Int, size: Int): Flux<AggregatedDomainOrder>

    fun create(order: CreateOrder): Mono<DomainOrder>

    fun deleteById(id: String): Mono<Unit>

    fun findByUserId(userId: String): Flux<DomainOrder>

    fun findByCarId(carId: String): Flux<DomainOrder>

    fun patch(id: String, patch: DomainOrder): Mono<DomainOrder>

    fun findByCarIdAndUserId(carId: String, userId: String): Flux<DomainOrder>

    fun findOrderByDateAndCarId(date: Date, carId: String): Mono<DomainOrder>
}
