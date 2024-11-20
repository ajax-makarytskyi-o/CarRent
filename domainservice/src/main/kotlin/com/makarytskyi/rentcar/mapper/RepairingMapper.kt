package com.makarytskyi.rentcar.mapper

import com.google.protobuf.Timestamp
import com.makarytskyi.commonmodels.repairing.Repairing
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.MongoRepairing.RepairingStatus
import com.makarytskyi.rentcar.util.Utils.dateToTimestamp

fun MongoRepairing.toProto(): Repairing = Repairing.newBuilder()
    .also {
        it.id = this.id?.toString().orEmpty()
        it.carId = this.carId?.toString().orEmpty()
        it.price = this.price?.toDouble() ?: 0.0
        it.date = this.date?.let { date -> dateToTimestamp(date) } ?: Timestamp.getDefaultInstance()
        it.status = this.status?.toProto() ?: Repairing.RepairingStatus.REPAIRING_STATUS_UNSPECIFIED
    }
    .build()

fun RepairingStatus.toProto(): Repairing.RepairingStatus =
    when (this) {
        RepairingStatus.PENDING -> Repairing.RepairingStatus.REPAIRING_STATUS_PENDING
        RepairingStatus.IN_PROGRESS -> Repairing.RepairingStatus.REPAIRING_STATUS_IN_PROGRESS
        RepairingStatus.COMPLETED -> Repairing.RepairingStatus.REPAIRING_STATUS_COMPLETED
    }
