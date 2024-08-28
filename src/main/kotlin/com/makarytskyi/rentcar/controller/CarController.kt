package com.makarytskyi.rentcar.controller

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.service.CarService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/car")
class CarController(private val service: CarService) {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): CarResponse = service.getById(id)

    @GetMapping("/all")
    fun findAll(): List<CarResponse> = service.findAll()

    @GetMapping("/brand/{brand}")
    fun findAllByBrand(@PathVariable brand: String): List<CarResponse> = service.findAllByBrand(brand)

    @GetMapping("/brand/{brand}/model/{model}")
    fun findAllByBrandAndModel(@PathVariable brand: String, @PathVariable model: String): List<CarResponse> =
        service.findAllByBrandAndModel(brand, model)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody car: CreateCarRequest): CarResponse = service.create(car)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) = service.deleteById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @Valid @RequestBody car: UpdateCarRequest): CarResponse =
        service.update(id, car)

    @GetMapping("/plate/{plate}")
    fun findByPlate(@PathVariable plate: String): CarResponse = service.getByPlate(plate)
}
