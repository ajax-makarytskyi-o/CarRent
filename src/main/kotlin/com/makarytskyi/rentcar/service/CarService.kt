package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import org.springframework.stereotype.Service

@Service
internal interface CarService {

    fun getById(id: String): CarResponse

    fun findAll(): List<CarResponse>

    fun create(carRequest: CreateCarRequest): CarResponse

    fun deleteById(id: String)

    fun findAllByBrand(brand: String): List<CarResponse>

    fun findAllByBrandAndModel(brand: String, model: String): List<CarResponse>

    fun update(id: String, carRequest: UpdateCarRequest): CarResponse

    fun getByPlate(plate: String): CarResponse
}
