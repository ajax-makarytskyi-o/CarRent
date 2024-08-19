package com.makarytskyi.rentcar.dto.repairing

import com.makarytskyi.rentcar.model.Repairing.RepairingStatus

data class UpdateRepairingRequest(
    val price: Int?,
    val status: RepairingStatus?
)
