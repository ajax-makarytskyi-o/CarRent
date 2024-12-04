package com.makarytskyi.rentcar.repairing.application.mapper

import com.makarytskyi.commonmodels.repairing.Repairing
import com.makarytskyi.rentcar.common.util.Utils.dateToTimestamp
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing.RepairingStatus

fun DomainRepairing.toProto(): Repairing = Repairing.newBuilder()
    .also {
        it.id = this.id.orEmpty()
        it.carId = this.carId
        it.price = this.price.toDouble()
        it.date = dateToTimestamp(date)
        it.status = this.status.toProto()
    }
    .build()

fun RepairingStatus.toProto(): Repairing.RepairingStatus =
    when (this) {
        RepairingStatus.PENDING -> Repairing.RepairingStatus.REPAIRING_STATUS_PENDING
        RepairingStatus.IN_PROGRESS -> Repairing.RepairingStatus.REPAIRING_STATUS_IN_PROGRESS
        RepairingStatus.COMPLETED -> Repairing.RepairingStatus.REPAIRING_STATUS_COMPLETED
    }
