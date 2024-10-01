package com.makarytskyi.rentcar.service.impl

import com.makarytskyi.rentcar.annotation.InvocationTracker
import com.makarytskyi.rentcar.dto.repairing.AggregatedRepairingResponse
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.exception.NotFoundException
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.RepairingRepository
import com.makarytskyi.rentcar.service.RepairingService
import java.util.Date
import org.springframework.stereotype.Service

@InvocationTracker
@Service
internal class RepairingServiceImpl(
    private val repairingRepository: RepairingRepository,
    private val carRepository: CarRepository,
) : RepairingService {

    override fun findAll(page: Int, size: Int): List<AggregatedRepairingResponse> =
        repairingRepository.findAll(page, size).map { AggregatedRepairingResponse.from(it) }.toList()

    override fun create(repairingRequest: CreateRepairingRequest): RepairingResponse {
        validateCarExists(repairingRequest.carId)
        validateDate(repairingRequest.date)
        return RepairingResponse.from(repairingRepository.create(CreateRepairingRequest.toEntity(repairingRequest)))
    }

    override fun getById(id: String): AggregatedRepairingResponse = repairingRepository.findById(id)
        ?.let { AggregatedRepairingResponse.from(it) }
        ?: throw NotFoundException("Repairing with id $id is not found")

    override fun deleteById(id: String) = repairingRepository.deleteById(id)

    override fun patch(id: String, repairingRequest: UpdateRepairingRequest): RepairingResponse =
        repairingRepository.patch(id, UpdateRepairingRequest.toPatch(repairingRequest))
            ?.let { RepairingResponse.from(it) }
            ?: throw NotFoundException("Repairing with id $id is not found")

    override fun findByStatus(status: MongoRepairing.RepairingStatus): List<RepairingResponse> =
        repairingRepository.findByStatus(status).map { RepairingResponse.from(it) }

    override fun findByCarId(carId: String): List<RepairingResponse> =
        repairingRepository.findByCarId(carId).map { RepairingResponse.from(it) }

    override fun findByStatusAndCar(status: MongoRepairing.RepairingStatus, carId: String): List<RepairingResponse> =
        repairingRepository.findByStatusAndCarId(status, carId).map { RepairingResponse.from(it) }

    private fun validateCarExists(carId: String) =
        require(carRepository.findById(carId) != null) { "Car in repairing with $carId is not found" }

    private fun validateDate(date: Date?) {
        require(date?.after(Date()) == true) { "Date must be in future" }
    }
}
