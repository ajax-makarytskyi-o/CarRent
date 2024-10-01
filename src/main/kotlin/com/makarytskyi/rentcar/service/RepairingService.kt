package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.repairing.AggregatedRepairingResponse
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.model.MongoRepairing

internal interface RepairingService {

    fun findAll(page: Int, size: Int): List<AggregatedRepairingResponse>

    fun create(repairingRequest: CreateRepairingRequest): RepairingResponse

    fun getById(id: String): AggregatedRepairingResponse

    fun deleteById(id: String)

    fun patch(id: String, repairingRequest: UpdateRepairingRequest): RepairingResponse

    fun findByStatus(status: MongoRepairing.RepairingStatus): List<RepairingResponse>

    fun findByCarId(carId: String): List<RepairingResponse>

    fun findByStatusAndCar(status: MongoRepairing.RepairingStatus, carId: String): List<RepairingResponse>
}
