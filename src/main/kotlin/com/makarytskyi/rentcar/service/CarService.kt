package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.exception.CarNotFoundException
import com.makarytskyi.rentcar.repository.impl.SimpleCarRepository
import org.springframework.stereotype.Service

@Service
class CarService(private val repository: SimpleCarRepository) {

    fun findById(id: String): CarResponse = repository.findById(id)?.toResponse()
        ?: throw CarNotFoundException("Car with id $id is not found")

    fun findAll(): List<CarResponse> = repository.findAll().map { it.toResponse() }

    fun save(car: CreateCarRequest): CarResponse = repository.findByPlate(car.plate)
        ?.let { throw IllegalArgumentException("Car with plate ${car.plate} is already exist") }
        ?: repository.save(car.toEntity()).toResponse()

    fun deleteById(id: String) {
        repository.findById(id)?.let { repository.deleteById(id) }
            ?: throw CarNotFoundException("Car with id $id is not found")
    }

    fun findAllByMark(mark: String): List<CarResponse> = repository.findAllByMark(mark).map { it.toResponse() }

    fun update(id: String, carRequest: UpdateCarRequest): CarResponse =
        repository.findById(id)?.let { repository.update(id, carRequest.price, carRequest.color) }?.toResponse()
            ?: throw CarNotFoundException("Car with id $id is not found")

    fun findByPlate(plate: String): CarResponse = repository.findByPlate(plate)?.toResponse()
        ?: throw CarNotFoundException("Car with plate $plate is not found")
}
