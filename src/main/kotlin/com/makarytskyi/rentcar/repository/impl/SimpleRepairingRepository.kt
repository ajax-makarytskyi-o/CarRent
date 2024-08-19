package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.Car
import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.model.Repairing.RepairingStatus
import com.makarytskyi.rentcar.repository.RepairingRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.util.*
import kotlin.collections.HashMap

@Repository
class SimpleRepairingRepository: RepairingRepository {
    val map: MutableMap<String, Repairing> = HashMap()

    override fun save(repairing: Repairing): Repairing {
        val id = ObjectId().toString()
        val savedRepairing = Repairing(
            id,
            repairing.carId,
            repairing.date,
            repairing.price,
            repairing.status
        )

        map[id] = savedRepairing
        return savedRepairing
    }

    override fun findById(id: String) = map[id]

    override fun findAll(): List<Repairing> = map.values.toList()

    override fun deleteById(id: String) = map.remove(id)

    override fun update(id: String, repairing: Repairing): Repairing? {
        val oldRepairing: Repairing? = findById(id)

        if (oldRepairing == null) {
            return null
        } else {
            val updatedCar: Repairing = Repairing(
                id,
                carId = repairing.carId ?: oldRepairing.carId,
                date = repairing.date ?: oldRepairing.date,
                price = repairing.price ?: oldRepairing.price,
                status = repairing.status ?: oldRepairing.status
            )
            map[id] = updatedCar
            return updatedCar
        }
    }

    override fun findByStatus(status: RepairingStatus): List<Repairing> = map.values.filter { it.status == status }

    override fun findByCarId(carId: String): List<Repairing> = map.values.filter { it.carId == carId }

    override fun updatePrice(id: String, price: Int): Repairing? {
        val oldRepairing: Repairing? = findById(id)

        if (oldRepairing == null) {
            return null
        } else {
            val updatedRepairing = Repairing(
                id,
                oldRepairing.carId,
                oldRepairing.date,
                price,
                oldRepairing.status
            )
            map[id] = updatedRepairing
            return updatedRepairing
        }
    }

    override fun updateStatus(id: String, status: RepairingStatus): Repairing? {
        val oldRepairing: Repairing? = findById(id)

        if (oldRepairing == null) {
            return null
        } else {
            val updatedRepairing = Repairing(
                id,
                oldRepairing.carId,
                oldRepairing.date,
                oldRepairing.price,
                status
            )
            map[id] = updatedRepairing
            return updatedRepairing
        }
    }
}
