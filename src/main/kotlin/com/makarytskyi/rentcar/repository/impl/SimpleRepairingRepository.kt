package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.model.Repairing.RepairingStatus
import com.makarytskyi.rentcar.repository.RepairingRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class SimpleRepairingRepository: RepairingRepository {
    val map: MutableMap<String, Repairing> = HashMap()

    override fun save(repairing: Repairing): Repairing {
        val id = ObjectId().toString()
        val savedRepairing = repairing.copy(id = id)
        map[id] = savedRepairing
        return savedRepairing
    }

    override fun findById(id: String) = map[id]

    override fun findAll(): List<Repairing> = map.values.toList()

    override fun deleteById(id: String) {
        map.remove(id)
    }

    override fun update(id: String, repairing: Repairing): Repairing? {
        val oldRepairing: Repairing? = findById(id)

        return oldRepairing?.let {
            val updatedCar = oldRepairing.copy(price = repairing.price, status = repairing.status)
            map[id] = updatedCar
            return updatedCar
        }
    }

    override fun findByStatus(status: RepairingStatus): List<Repairing> = map.values.filter { it.status == status }

    override fun findByCarId(carId: String): List<Repairing> = map.values.filter { it.carId == carId }

    override fun updatePrice(id: String, price: Int): Repairing? {
        val oldRepairing: Repairing? = findById(id)

        return oldRepairing?.let {
            val updatedRepairing = oldRepairing.copy(price = price)
            map[id] = updatedRepairing
            return updatedRepairing
        }
    }

    override fun updateStatus(id: String, status: RepairingStatus): Repairing? {
        val oldRepairing: Repairing? = findById(id)

        return oldRepairing?.let {
            val updatedRepairing = oldRepairing.copy(status = status)
            map[id] = updatedRepairing
            return updatedRepairing
        }
    }
}
