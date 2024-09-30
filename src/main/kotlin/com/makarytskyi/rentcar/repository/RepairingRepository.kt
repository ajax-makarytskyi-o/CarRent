package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.projection.AggregatedMongoRepairing
import org.springframework.stereotype.Repository

@Repository
internal interface RepairingRepository {

    fun create(mongoRepairing: MongoRepairing): MongoRepairing

    fun findById(id: String): AggregatedMongoRepairing?

    fun findAll(): List<AggregatedMongoRepairing>

    fun deleteById(id: String)

    fun update(id: String, mongoRepairing: MongoRepairing): MongoRepairing?

    fun findByStatus(status: MongoRepairing.RepairingStatus): List<MongoRepairing>

    fun findByCarId(carId: String): List<MongoRepairing>

    fun findByStatusAndCarId(status: MongoRepairing.RepairingStatus, carId: String): List<MongoRepairing>
}
