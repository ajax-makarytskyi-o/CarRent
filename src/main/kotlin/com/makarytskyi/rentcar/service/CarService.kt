package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.model.Car
import com.makarytskyi.rentcar.repository.CarRepository
import org.springframework.stereotype.Service

@Service
internal class CarService(private val repository: CarRepository) {

    fun getById(id: String): CarResponse = repository.findById(id)?.let { Car.toResponse(it) }
        ?: throw ResourceNotFoundException("Car with id $id is not found")

    fun findAll(): List<CarResponse> = repository.findAll().map { Car.toResponse(it) }

    fun create(carRequest: CreateCarRequest): CarResponse {
        validatePlate(carRequest.plate)
        val car = repository.create(CreateCarRequest.toEntity(carRequest))
        return Car.toResponse(car)
    }

    fun deleteById(id: String) = repository.deleteById(id)

    fun findAllByBrand(brand: String): List<CarResponse> = repository.findAllByBrand(brand).map { Car.toResponse(it) }

    fun findAllByBrandAndModel(brand: String, model: String): List<CarResponse> =
        repository.findAllByBrandAndModel(brand, model).map { Car.toResponse(it) }

    fun update(id: String, carRequest: UpdateCarRequest): CarResponse =
        repository.update(id, UpdateCarRequest.toEntity(carRequest))?.let { Car.toResponse(it) }
            ?: throw ResourceNotFoundException("Car with id $id is not found")

    fun getByPlate(plate: String): CarResponse = repository.findByPlate(plate)?.let { Car.toResponse(it) }
        ?: throw ResourceNotFoundException("Car with plate $plate is not found")

    private fun validatePlate(plate: String) {
        require(repository.findByPlate(plate) == null) { "Car with plate $plate is already exist" }
    }
}
