package com.makarytskyi.rentcar.service.impl

import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.exception.CarNotFoundException
import com.makarytskyi.rentcar.exception.RepairingNotFoundException
import com.makarytskyi.rentcar.model.Car
import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.impl.SimpleRepairingRepository
import org.springframework.stereotype.Service

@Service
class RepairingService(
    val repairingRepository: SimpleRepairingRepository,
    val carRepository: CarRepository
) {

    fun findAll(): List<RepairingResponse> = repairingRepository.findAll().map { it.toResponse() }.toList()

    fun save(repairing: CreateRepairingRequest): RepairingResponse {
        if (carRepository.findById(repairing.carId) == null)
            throw IllegalArgumentException("Car in repairing with ${repairing.carId} is not found")

        return repairingRepository.save(repairing.toEntity()).toResponse()
    }

    fun findById(id: String): RepairingResponse = repairingRepository.findById(id)?.toResponse()
        ?: throw RepairingNotFoundException("Repairing with id $id is not found")

    fun deleteById(id: String) = repairingRepository.deleteById(id)

    fun update(id: String, repairingRequest: UpdateRepairingRequest): RepairingResponse {
        if (repairingRepository.findById(id) == null)
            throw CarNotFoundException("Car with id $id is not found")

        var updatedCar: Repairing? = null

        if (repairingRequest.price != null)
            updatedCar = repairingRepository.updatePrice(id, repairingRequest.price)

        if (repairingRequest.status != null)
            updatedCar = repairingRepository.updateStatus(id, repairingRequest.status)

        if (updatedCar == null)
            throw IllegalArgumentException("Fields price or/and color must be set")

        return updatedCar.toResponse()
    }

    fun findByStatus(status: Repairing.RepairingStatus): List<RepairingResponse> = repairingRepository.findByStatus(status).map { it.toResponse() }

    fun findByCarId(carId: String): List<RepairingResponse> = repairingRepository.findByCarId(carId).map { it.toResponse() }
}
