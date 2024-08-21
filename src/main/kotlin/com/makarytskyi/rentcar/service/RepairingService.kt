package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.exception.RepairingNotFoundException
import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.RepairingRepository
import org.springframework.stereotype.Service

@Service
class RepairingService(
    private val repairingRepository: RepairingRepository,
    private val carRepository: CarRepository
) {

    fun findAll(): List<RepairingResponse> = repairingRepository.findAll().map { Repairing.toResponse(it) }.toList()

    fun create(repairingRequest: CreateRepairingRequest): RepairingResponse {
        validateCarExists(repairingRequest.carId)
        return Repairing.toResponse(repairingRepository.create(CreateRepairingRequest.toEntity(repairingRequest)))
    }

    fun getById(id: String): RepairingResponse = repairingRepository.findById(id)?.let { Repairing.toResponse(it) }
        ?: throw RepairingNotFoundException("Repairing with id $id is not found")

    fun deleteById(id: String) = repairingRepository.deleteById(id)

    fun update(id: String, repairingRequest: UpdateRepairingRequest): RepairingResponse {
        validateNotEmptyRequest(repairingRequest)

        return repairingRepository.update(id, UpdateRepairingRequest.toEntity(repairingRequest))
            ?.let { Repairing.toResponse(it) }
            ?: throw RepairingNotFoundException("Repairing with id $id is not found")
    }

    fun findByStatus(status: Repairing.RepairingStatus): List<RepairingResponse> =
        repairingRepository.findByStatus(status).map { Repairing.toResponse(it) }

    fun findByCarId(carId: String): List<RepairingResponse> =
        repairingRepository.findByCarId(carId).map { Repairing.toResponse(it) }

    private fun validateCarExists(carId: String) {
        require(carRepository.findById(carId) != null) { "Car in repairing with $carId is not found" }
    }

    private fun validateNotEmptyRequest(repairingRequest: UpdateRepairingRequest) {
        require(repairingRequest.status != null || repairingRequest.price != null) { "Update request is empty" }
    }
}
