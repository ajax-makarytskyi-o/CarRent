package com.makarytskyi.rentcar.car.infrastructure.rest

import com.makarytskyi.rentcar.car.application.port.input.CarInputPort
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.CarResponse
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.CreateCarRequest
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.UpdateCarRequest
import com.makarytskyi.rentcar.car.infrastructure.rest.mapper.toDomain
import com.makarytskyi.rentcar.car.infrastructure.rest.mapper.toPatch
import com.makarytskyi.rentcar.car.infrastructure.rest.mapper.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/cars")
internal class CarController(private val carInputPort: CarInputPort) {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): Mono<CarResponse> = carInputPort.getById(id).map { it.toResponse() }

    @GetMapping()
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): Flux<CarResponse> = carInputPort.findAll(page, size).map { it.toResponse() }

    @GetMapping("/brand/{brand}")
    fun findAllByBrand(@PathVariable brand: String): Flux<CarResponse> =
        carInputPort.findAllByBrand(brand).map { it.toResponse() }

    @GetMapping("/brand/{brand}/model/{model}")
    fun findAllByBrandAndModel(@PathVariable brand: String, @PathVariable model: String): Flux<CarResponse> =
        carInputPort.findAllByBrandAndModel(brand, model).map { it.toResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody car: CreateCarRequest): Mono<CarResponse> =
        carInputPort.create(car.toDomain()).map { it.toResponse() }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String): Mono<Unit> = carInputPort.deleteById(id)

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: String, @Valid @RequestBody car: UpdateCarRequest): Mono<CarResponse> =
        carInputPort.patch(id, car.toPatch()).map { it.toResponse() }

    @GetMapping("/plate/{plate}")
    fun getByPlate(@PathVariable plate: String): Mono<CarResponse> =
        carInputPort.getByPlate(plate).map { it.toResponse() }
}
