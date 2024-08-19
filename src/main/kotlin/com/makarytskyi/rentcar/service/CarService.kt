package com.makarytskyi.rentcar.service.impl

import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.exception.CarNotFoundException
import com.makarytskyi.rentcar.model.Car
import com.makarytskyi.rentcar.repository.impl.SimpleCarRepository
import org.springframework.stereotype.Service

@Service
class CarService(val repository: SimpleCarRepository) {

    fun findById(id: String): CarResponse = repository.findById(id)?.toResponse()
        ?: throw CarNotFoundException("Car with id $id is not found")

    fun findAll(): List<CarResponse> = repository.findAll().map { it.toResponse() }

    fun save(car: CreateCarRequest): CarResponse {
        if (repository.findByPlate(car.plate) == null)
            return repository.save(car.toEntity()).toResponse()
        else
            throw IllegalArgumentException("Car with plate ${car.plate} is already exist")
    }

    fun deleteById(id: String) {
        if (repository.findById(id) != null)
            repository.deleteById(id)
        else
            throw CarNotFoundException("Car with id $id is not found")
    }

    fun findAllByMark(mark: String): List<CarResponse> = repository.findAllByMark(mark).map { it.toResponse() }

    fun update(id: String, carRequest: UpdateCarRequest): CarResponse {
        if (repository.findById(id) == null)
            throw CarNotFoundException("Car with id $id is not found")

        var updatedCar: Car? = null

        if (carRequest.price != null)
            updatedCar = repository.updatePrice(id, carRequest.price)

        if (carRequest.color != null)
            updatedCar = repository.updateColor(id, carRequest.color)

        if (updatedCar == null)
            throw IllegalArgumentException("Fields price or/and color must be set")

        return updatedCar.toResponse()
    }

    fun findByPlate(plate: String): CarResponse = repository.findByPlate(plate)?.toResponse()
        ?: throw CarNotFoundException("Car with plate $plate is not found")
}
