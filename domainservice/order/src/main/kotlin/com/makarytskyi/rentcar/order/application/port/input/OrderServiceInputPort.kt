package com.makarytskyi.rentcar.order.application.port.input

import com.makarytskyi.rentcar.order.domain.DomainOrder
import com.makarytskyi.rentcar.order.domain.create.CreateOrder
import com.makarytskyi.rentcar.order.domain.patch.PatchOrder
import com.makarytskyi.rentcar.order.domain.projection.AggregatedDomainOrder
import java.util.Date
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderServiceInputPort {
    fun getById(id: String): Mono<AggregatedDomainOrder>

    fun findAll(page: Int, size: Int): Flux<AggregatedDomainOrder>

    fun create(order: CreateOrder): Mono<DomainOrder>

    fun deleteById(id: String): Mono<Unit>

    fun findByUser(userId: String): Flux<DomainOrder>

    fun findByCar(carId: String): Flux<DomainOrder>

    fun findByCarAndUser(carId: String, userId: String): Flux<DomainOrder>

    fun patch(id: String, patch: PatchOrder): Mono<DomainOrder>

    fun findOrderByCarAndDate(carId: String, date: Date): Mono<DomainOrder>
}
