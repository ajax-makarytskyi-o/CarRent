package com.makarytskyi.rentcar.repairing.infrastructure.rest.mapper

import com.makarytskyi.rentcar.car.infrastructure.rest.mapper.toResponse
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.domain.patch.DomainRepairingPatch
import com.makarytskyi.rentcar.repairing.domain.projection.AggregatedDomainRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.AggregatedRepairingResponse
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.CreateRepairingRequest
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.RepairingResponse
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.UpdateRepairingRequest

fun AggregatedDomainRepairing.toResponse(): AggregatedRepairingResponse = AggregatedRepairingResponse(
    requireNotNull(this.id) { "Repairing id is null" },
    this.car.toResponse(),
    this.date,
    this.price,
    this.status.toResponse(),
)

fun CreateRepairingRequest.toDomain(): DomainRepairing = DomainRepairing(
    carId = this.carId,
    date = this.date ?: throw IllegalArgumentException("Date of repairing is null"),
    price = this.price ?: throw IllegalArgumentException("Price of repairing is null"),
    status = this.status?.toDomain() ?: DomainRepairing.RepairingStatus.PENDING,
)

fun DomainRepairing.toResponse(): RepairingResponse = RepairingResponse(
    requireNotNull(this.id) { "Repairing id is null" },
    requireNotNull(this.carId) { "Car id is null" },
    this.date,
    this.price,
    this.status.toResponse(),
)

fun UpdateRepairingRequest.toPatch() = DomainRepairingPatch(
    price = this.price,
    status = this.status,
)

fun CreateRepairingRequest.RepairingStatus.toDomain(): DomainRepairing.RepairingStatus =
    when (this) {
        CreateRepairingRequest.RepairingStatus.PENDING -> DomainRepairing.RepairingStatus.PENDING
        CreateRepairingRequest.RepairingStatus.IN_PROGRESS -> DomainRepairing.RepairingStatus.IN_PROGRESS
        CreateRepairingRequest.RepairingStatus.COMPLETED -> DomainRepairing.RepairingStatus.COMPLETED
    }

fun DomainRepairing.RepairingStatus.toResponse(): RepairingResponse.RepairingStatus =
    when (this) {
        DomainRepairing.RepairingStatus.PENDING -> RepairingResponse.RepairingStatus.PENDING
        DomainRepairing.RepairingStatus.IN_PROGRESS -> RepairingResponse.RepairingStatus.IN_PROGRESS
        DomainRepairing.RepairingStatus.COMPLETED -> RepairingResponse.RepairingStatus.COMPLETED
    }
