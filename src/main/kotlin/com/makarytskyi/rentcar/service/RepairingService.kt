package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.exception.RepairingNotFoundException
import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.impl.SimpleRepairingRepository
import org.springframework.stereotype.Service

@Service
class RepairingService(
    private val repairingRepository: SimpleRepairingRepository,
    private val carRepository: CarRepository
) {

    fun findAll(): List<RepairingResponse> = repairingRepository.findAll().map { it.toResponse() }.toList()

    fun save(repairing: CreateRepairingRequest): RepairingResponse =
        carRepository.findById(repairing.carId)?.let { repairingRepository.save(repairing.toEntity()).toResponse() }
            ?: throw IllegalArgumentException("Car in repairing with ${repairing.carId} is not found")

    fun findById(id: String): RepairingResponse = repairingRepository.findById(id)?.toResponse()
        ?: throw RepairingNotFoundException("Repairing with id $id is not found")

    fun deleteById(id: String) = repairingRepository.deleteById(id)

    fun update(id: String, repairingRequest: UpdateRepairingRequest) =
        repairingRepository.findById(id)?.let { repairingRepository.update(id, repairingRequest.toEntity()) }
            ?.toResponse()
            ?: throw RepairingNotFoundException("Repairing with id $id is not found")

    fun findByStatus(status: Repairing.RepairingStatus): List<RepairingResponse> =
        repairingRepository.findByStatus(status).map { it.toResponse() }

    fun findByCarId(carId: String): List<RepairingResponse> =
        repairingRepository.findByCarId(carId).map { it.toResponse() }
}
