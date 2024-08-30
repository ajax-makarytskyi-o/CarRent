package com.makarytskyi.rentcar.model

import java.util.Date

data class Repairing(
    val id: String? = null,
    val carId: String?,
    val date: Date?,
    val price: Int?,
    val status: RepairingStatus?,
) {

    enum class RepairingStatus {
        PENDING, IN_PROGRESS, COMPLETED;
    }
}
