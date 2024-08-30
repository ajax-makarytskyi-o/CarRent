package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.model.Repairing.RepairingStatus
import com.makarytskyi.rentcar.repository.RepairingRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
internal class InMemoryRepairingRepository: RepairingRepository {
    private val map: MutableMap<String, Repairing> = HashMap()

    override fun create(repairing: Repairing): Repairing {
        val id = ObjectId().toHexString()
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
            val updatedCar = oldRepairing.copy(
                price = repairing.price ?: oldRepairing.price,
                status = repairing.status ?: oldRepairing.status,
            )
            map[id] = updatedCar
            return updatedCar
        }
    }

    override fun findByStatusAndCarId(status: RepairingStatus, carId: String): List<Repairing> =
        map.values.filter { it.status == status && it.carId == carId }

    override fun findByStatus(status: RepairingStatus): List<Repairing> = map.values.filter { it.status == status }

    override fun findByCarId(carId: String): List<Repairing> = map.values.filter { it.carId == carId }
}
