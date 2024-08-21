package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.Repairing
import org.springframework.stereotype.Repository

@Repository
interface RepairingRepository {

    fun create(repairing: Repairing): Repairing

    fun findById(id: String): Repairing?

    fun findAll(): List<Repairing>

    fun deleteById(id: String)

    fun update(id: String, repairing: Repairing): Repairing?

    fun findByStatus(status: Repairing.RepairingStatus): List<Repairing>

    fun findByCarId(carId: String): List<Repairing>
}
