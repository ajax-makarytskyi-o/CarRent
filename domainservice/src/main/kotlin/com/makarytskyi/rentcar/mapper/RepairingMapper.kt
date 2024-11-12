package com.makarytskyi.rentcar.mapper

import com.makarytskyi.internalapi.commonmodels.repairing.Repairing
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.MongoRepairing.RepairingStatus
import com.makarytskyi.rentcar.util.dateToTimestamp

fun MongoRepairing.toProto(): Repairing = Repairing.newBuilder()
    .also {
        it.setId(this.id.toString())
        it.setCarId(this.carId.toString())
        it.setPrice(this.price?.toDouble() ?: 0.0)
        it.setDate(this.date?.let { date -> dateToTimestamp(date) })
        it.setStatus(this.status?.toProto())
    }
    .build()

fun RepairingStatus.toProto(): Repairing.RepairingStatus =
    when (this) {
        RepairingStatus.PENDING -> Repairing.RepairingStatus.PENDING
        RepairingStatus.IN_PROGRESS -> Repairing.RepairingStatus.IN_PROGRESS
        RepairingStatus.COMPLETED -> Repairing.RepairingStatus.COMPLETED
    }
