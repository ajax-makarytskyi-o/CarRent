package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.model.Car
import org.springframework.stereotype.Repository

@Repository
interface CarRepository {

    fun findById(id: String): Car?

    fun findAll(): List<Car>

    fun create(car: Car): Car

    fun deleteById(id: String)

    fun update(id: String, car: Car): Car?

    fun findTheMostExpensiveAvailableCar(): Car?

    fun findTheCheapestAvailableCar(): Car?

    fun findByPlate(plate: String?): Car?

    fun findAllByBrand(brand: String): List<Car>

    fun findAllByBrandAndModel(brand: String, model: String): List<Car>
}
