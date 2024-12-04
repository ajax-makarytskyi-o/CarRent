package com.makarytskyi.rentcar.car.application.port.input

import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.domain.create.CreateCar
import com.makarytskyi.rentcar.car.domain.patch.PatchCar
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CarServiceInputPort {
    fun getById(id: String): Mono<DomainCar>

    fun findAll(page: Int, size: Int): Flux<DomainCar>

    fun create(car: CreateCar): Mono<DomainCar>

    fun deleteById(id: String): Mono<Unit>

    fun findAllByBrand(brand: String): Flux<DomainCar>

    fun findAllByBrandAndModel(brand: String, model: String): Flux<DomainCar>

    fun patch(id: String, patch: PatchCar): Mono<DomainCar>

    fun getByPlate(plate: String): Mono<DomainCar>
}
