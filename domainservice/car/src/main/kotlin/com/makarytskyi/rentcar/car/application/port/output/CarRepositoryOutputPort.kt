package com.makarytskyi.rentcar.car.application.port.output

import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.domain.create.CreateCar
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CarRepositoryOutputPort {
    fun findById(id: String): Mono<DomainCar>

    fun findAll(page: Int, size: Int): Flux<DomainCar>

    fun create(car: CreateCar): Mono<DomainCar>

    fun deleteById(id: String): Mono<Unit>

    fun patch(id: String, patch: DomainCar): Mono<DomainCar>

    fun findByPlate(plate: String): Mono<DomainCar>

    fun findAllByBrand(brand: String): Flux<DomainCar>

    fun findAllByBrandAndModel(brand: String, model: String): Flux<DomainCar>
}
