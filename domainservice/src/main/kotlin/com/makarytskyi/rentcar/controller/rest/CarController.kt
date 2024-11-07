package com.makarytskyi.rentcar.controller.rest

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.service.CarService
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
internal class CarController(private val service: CarService) {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): Mono<CarResponse> = service.getById(id)

    @GetMapping()
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): Flux<CarResponse> = service.findAll(page, size)

    @GetMapping("/brand/{brand}")
    fun findAllByBrand(@PathVariable brand: String): Flux<CarResponse> = service.findAllByBrand(brand)

    @GetMapping("/brand/{brand}/model/{model}")
    fun findAllByBrandAndModel(@PathVariable brand: String, @PathVariable model: String): Flux<CarResponse> =
        service.findAllByBrandAndModel(brand, model)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody car: CreateCarRequest): Mono<CarResponse> = service.create(car)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String): Mono<Unit> = service.deleteById(id)

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: String, @Valid @RequestBody car: UpdateCarRequest): Mono<CarResponse> =
        service.patch(id, car)

    @GetMapping("/plate/{plate}")
    fun getByPlate(@PathVariable plate: String): Mono<CarResponse> = service.getByPlate(plate)
}
