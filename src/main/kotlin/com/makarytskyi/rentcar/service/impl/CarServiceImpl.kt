package com.makarytskyi.rentcar.service.impl

import com.makarytskyi.rentcar.annotation.InvocationTracker
import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.service.CarService
import org.springframework.stereotype.Service

@InvocationTracker
@Service
internal class CarServiceImpl(private val repository: CarRepository) : CarService {

    override fun getById(id: String): CarResponse = repository.findById(id)?.let { CarResponse.from(it) }
        ?: throw ResourceNotFoundException("Car with id $id is not found")

    override fun findAll(): List<CarResponse> = repository.findAll().map { CarResponse.from(it) }

    override fun create(carRequest: CreateCarRequest): CarResponse {
        validatePlate(carRequest.plate)
        val car = repository.create(CreateCarRequest.toEntity(carRequest))
        return CarResponse.from(car)
    }

    override fun deleteById(id: String) = repository.deleteById(id)

    override fun findAllByBrand(brand: String): List<CarResponse> =
        repository.findAllByBrand(brand).map { CarResponse.from(it) }

    override fun findAllByBrandAndModel(brand: String, model: String): List<CarResponse> =
        repository.findAllByBrandAndModel(brand, model).map { CarResponse.from(it) }

    override fun update(id: String, carRequest: UpdateCarRequest): CarResponse =
        repository.update(id, UpdateCarRequest.toEntity(carRequest))?.let { CarResponse.from(it) }
            ?: throw ResourceNotFoundException("Car with id $id is not found")

    override fun getByPlate(plate: String): CarResponse = repository.findByPlate(plate)?.let { CarResponse.from(it) }
        ?: throw ResourceNotFoundException("Car with plate $plate is not found")

    private fun validatePlate(plate: String) {
        require(repository.findByPlate(plate) == null) { "Car with plate $plate is already exist" }
    }
}
