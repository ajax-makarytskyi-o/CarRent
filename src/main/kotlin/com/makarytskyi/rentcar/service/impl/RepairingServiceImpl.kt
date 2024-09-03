package com.makarytskyi.rentcar.service.impl

import com.makarytskyi.rentcar.annotation.InvocationTracker
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.RepairingRepository
import com.makarytskyi.rentcar.service.RepairingService
import org.springframework.stereotype.Service

@InvocationTracker
@Service
internal class RepairingServiceImpl(
    private val repairingRepository: RepairingRepository,
    private val carRepository: CarRepository
) : RepairingService {

    override fun findAll(): List<RepairingResponse> =
        repairingRepository.findAll().map { RepairingResponse.from(it) }.toList()

    override fun create(repairingRequest: CreateRepairingRequest): RepairingResponse {
        validateCarExists(repairingRequest.carId)
        return RepairingResponse.from(repairingRepository.create(CreateRepairingRequest.toEntity(repairingRequest)))
    }

    override fun getById(id: String): RepairingResponse = repairingRepository.findById(id)
        ?.let { RepairingResponse.from(it) }
        ?: throw ResourceNotFoundException("Repairing with id $id is not found")

    override fun deleteById(id: String) = repairingRepository.deleteById(id)

    override fun update(id: String, repairingRequest: UpdateRepairingRequest): RepairingResponse =
        repairingRepository.update(id, UpdateRepairingRequest.toEntity(repairingRequest))
            ?.let { RepairingResponse.from(it) }
            ?: throw ResourceNotFoundException("Repairing with id $id is not found")

    override fun findByStatus(status: Repairing.RepairingStatus): List<RepairingResponse> =
        repairingRepository.findByStatus(status).map { RepairingResponse.from(it) }

    override fun findByCarId(carId: String): List<RepairingResponse> =
        repairingRepository.findByCarId(carId).map { RepairingResponse.from(it) }

    override fun findByStatusAndCar(status: Repairing.RepairingStatus, carId: String): List<RepairingResponse> =
        repairingRepository.findByStatusAndCarId(status, carId).map { RepairingResponse.from(it) }

    private fun validateCarExists(carId: String) =
        require(carRepository.findById(carId) != null) { "Car in repairing with $carId is not found" }
}
