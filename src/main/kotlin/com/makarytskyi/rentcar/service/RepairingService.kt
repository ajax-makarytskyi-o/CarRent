package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.model.Repairing
import org.springframework.stereotype.Service

@Service
internal interface RepairingService {

    fun findAll(): List<RepairingResponse>

    fun create(repairingRequest: CreateRepairingRequest): RepairingResponse

    fun getById(id: String): RepairingResponse

    fun deleteById(id: String)

    fun update(id: String, repairingRequest: UpdateRepairingRequest): RepairingResponse

    fun findByStatus(status: Repairing.RepairingStatus): List<RepairingResponse>

    fun findByCarId(carId: String): List<RepairingResponse>

    fun findByStatusAndCar(status: Repairing.RepairingStatus, carId: String): List<RepairingResponse>
}
