package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.Car
import com.makarytskyi.rentcar.repository.CarRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class SimpleCarRepository: CarRepository {
    val map: MutableMap<String, Car> = HashMap()

    override fun findById(id: String?): Car? = map[id]

    override fun save(car: Car): Car {
        val id = ObjectId().toString()
        val savedCar = Car(
            id,
            car.mark,
            car.model,
            car.price,
            car.year,
            car.plate,
            car.color
        )

        map[id] = savedCar
        return savedCar
    }

    override fun deleteById(id: String) = map.remove(id)

    override fun findAll(): List<Car> = map.values.toList()

    override fun findTheMostExpensiveAvailableCar(): Car? = map.values.filter { it.price != null }.maxBy { it.price!! }

    override fun findTheCheapestAvailableCar(): Car? = map.values.filter { it.price != null }.minBy { it.price!! }

    override fun findAllByMark(mark: String): List<Car> = map.values.filter { it.mark == mark }

    override fun updatePrice(id: String, price: Int): Car? {
        val oldCar: Car? = findById(id)

        if (oldCar == null) {
            return null
        } else {
            val updatedCar = Car(
                oldCar.id,
                oldCar.mark,
                oldCar.model,
                price,
                oldCar.year,
                oldCar.plate,
                oldCar.color
            )
            map[id] = updatedCar
            return updatedCar
        }
    }

    override fun findByPlate(plate: String?): Car? = map.values.find { it.plate == plate }

    override fun updateColor(id: String, color: Car.CarColor): Car? {
        val oldCar: Car? = findById(id)

        if (oldCar == null) {
            return null
        } else {
            val updatedCar = Car(
                oldCar.id,
                oldCar.mark,
                oldCar.model,
                oldCar.price,
                oldCar.year,
                oldCar.plate,
                color
            )
            map[id] = updatedCar
            return updatedCar
        }
    }
}
