package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.patch.MongoCarPatch
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
internal interface CarRepository {

    fun findById(id: String): Mono<MongoCar>

    fun findAll(page: Int, size: Int): Flux<MongoCar>

    fun create(mongoCar: MongoCar): Mono<MongoCar>

    fun deleteById(id: String): Mono<Unit>

    fun patch(id: String, carPatch: MongoCarPatch): Mono<MongoCar>

    fun findByPlate(plate: String): Mono<MongoCar>

    fun findAllByBrand(brand: String): Flux<MongoCar>

    fun findAllByBrandAndModel(brand: String, model: String): Flux<MongoCar>
}
