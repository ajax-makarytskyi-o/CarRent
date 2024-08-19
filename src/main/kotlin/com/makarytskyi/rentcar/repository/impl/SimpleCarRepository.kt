package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.Car
import com.makarytskyi.rentcar.repository.CarRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class SimpleCarRepository: CarRepository {
    val map: MutableMap<String, Car> = HashMap()

    override fun findById(id: String): Car? = map[id]

    override fun save(car: Car): Car {
        val id = ObjectId().toString()
        val savedCar = car.copy(id = id)
        map[id] = savedCar
        return savedCar
    }

    override fun deleteById(id: String) {
        map.remove(id)
    }

    override fun findAll(): List<Car> = map.values.toList()

    override fun findTheMostExpensiveAvailableCar(): Car? = map.values.filter { it.price != null }.maxBy { it.price!! }

    override fun findTheCheapestAvailableCar(): Car? = map.values.filter { it.price != null }.minBy { it.price!! }

    override fun findAllByMark(mark: String): List<Car> = map.values.filter { it.mark == mark }

    override fun update(id: String, price: Int, color: Car.CarColor): Car? {
        val oldCar: Car? = findById(id)

        return oldCar?.let {
            val updatedCar = oldCar.copy(price = price, color = color)
            map[id] = updatedCar
            return updatedCar
        }
    }

    override fun findByPlate(plate: String?): Car? = map.values.find { it.plate == plate }
}
