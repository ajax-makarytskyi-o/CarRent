package com.makarytskyi.rentcar.car.application.service

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.rentcar.car.application.port.input.CarServiceInputPort
import com.makarytskyi.rentcar.car.application.port.output.CarRepositoryOutputPort
import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.domain.create.CreateCar
import com.makarytskyi.rentcar.car.domain.patch.PatchCar
import com.makarytskyi.rentcar.common.annotation.InvocationTracker
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@InvocationTracker
@Service
class CarService(private val carOutputPort: CarRepositoryOutputPort) : CarServiceInputPort {
    override fun getById(id: String): Mono<DomainCar> = carOutputPort.findById(id)
        .switchIfEmpty { Mono.error(NotFoundException("Car with id $id is not found")) }

    override fun findAll(page: Int, size: Int): Flux<DomainCar> = carOutputPort.findAll(page, size)

    override fun create(car: CreateCar): Mono<DomainCar> =
        carOutputPort.create(car)
            .onErrorMap(DuplicateKeyException::class.java) {
                IllegalArgumentException("Car with plate ${car.plate} already exists")
            }

    override fun deleteById(id: String): Mono<Unit> = carOutputPort.deleteById(id)

    override fun findAllByBrand(brand: String): Flux<DomainCar> =
        carOutputPort.findAllByBrand(brand)

    override fun findAllByBrandAndModel(brand: String, model: String): Flux<DomainCar> =
        carOutputPort.findAllByBrandAndModel(brand, model)

    override fun patch(id: String, patch: PatchCar): Mono<DomainCar> =
        carOutputPort.findById(id)
            .flatMap { carOutputPort.patch(id, it.fromPatch(patch)) }
            .switchIfEmpty { Mono.error(NotFoundException("Car with id $id is not found")) }

    override fun getByPlate(plate: String): Mono<DomainCar> = carOutputPort.findByPlate(plate)
        .switchIfEmpty { Mono.error(NotFoundException("Car with plate $plate is not found")) }
}
