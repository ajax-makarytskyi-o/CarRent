package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.Car
import org.springframework.stereotype.Repository

@Repository
interface CarRepository {

    fun findById(id: String?): Car?

    fun save(car: Car): Car

    fun deleteById(id: String): Car?

    fun findAll(): List<Car>

    fun findTheMostExpensiveAvailableCar(): Car?

    fun findTheCheapestAvailableCar(): Car?

    fun findAllByMark(mark: String): List<Car>

    fun findByPlate(plate: String?): Car?

    fun updatePrice(id: String, price: Int): Car?

    fun updateColor(id: String, color: Car.CarColor): Car?
}
