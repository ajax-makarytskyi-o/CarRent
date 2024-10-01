package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest

internal interface CarService {

    fun getById(id: String): CarResponse

    fun findAll(page: Int, size: Int): List<CarResponse>

    fun create(carRequest: CreateCarRequest): CarResponse

    fun deleteById(id: String)

    fun findAllByBrand(brand: String): List<CarResponse>

    fun findAllByBrandAndModel(brand: String, model: String): List<CarResponse>

    fun patch(id: String, carRequest: UpdateCarRequest): CarResponse

    fun getByPlate(plate: String): CarResponse
}
