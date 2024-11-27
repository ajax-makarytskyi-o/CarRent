package com.makarytskyi.rentcar.service.impl

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.rentcar.annotation.InvocationTracker
import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.service.CarService
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@InvocationTracker
@Service
internal class CarServiceImpl(
    private val repository: CarRepository,
) : CarService {

    override fun getById(id: String): Mono<CarResponse> = repository.findById(id)
        .switchIfEmpty { Mono.error(NotFoundException("Car with id $id is not found")) }
        .map { CarResponse.from(it) }

    override fun findAll(page: Int, size: Int): Flux<CarResponse> = repository.findAll(page, size)
        .map { CarResponse.from(it) }

    override fun create(carRequest: CreateCarRequest): Mono<CarResponse> =
        repository.create(CreateCarRequest.toEntity(carRequest))
            .onErrorMap(DuplicateKeyException::class.java) {
                IllegalArgumentException("Car with plate ${carRequest.plate} already exists")
            }
            .map { CarResponse.from(it) }

    override fun deleteById(id: String): Mono<Unit> = repository.deleteById(id)

    override fun findAllByBrand(brand: String): Flux<CarResponse> =
        repository.findAllByBrand(brand).map { CarResponse.from(it) }

    override fun findAllByBrandAndModel(brand: String, model: String): Flux<CarResponse> =
        repository.findAllByBrandAndModel(brand, model).map { CarResponse.from(it) }

    override fun patch(id: String, carRequest: UpdateCarRequest): Mono<CarResponse> =
        repository.patch(id, UpdateCarRequest.toPatch(carRequest))
            .switchIfEmpty { Mono.error(NotFoundException("Car with id $id is not found")) }
            .map { CarResponse.from(it) }

    override fun getByPlate(plate: String): Mono<CarResponse> = repository.findByPlate(plate)
        .switchIfEmpty { Mono.error(NotFoundException("Car with plate $plate is not found")) }
        .map { CarResponse.from(it) }
}
