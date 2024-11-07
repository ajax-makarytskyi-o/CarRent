package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

internal interface CarService {

    fun getById(id: String): Mono<CarResponse>

    fun findAll(page: Int, size: Int): Flux<CarResponse>

    fun create(carRequest: CreateCarRequest): Mono<CarResponse>

    fun deleteById(id: String): Mono<Unit>

    fun findAllByBrand(brand: String): Flux<CarResponse>

    fun findAllByBrandAndModel(brand: String, model: String): Flux<CarResponse>

    fun patch(id: String, carRequest: UpdateCarRequest): Mono<CarResponse>

    fun getByPlate(plate: String): Mono<CarResponse>
}
