package com.makarytskyi.rentcar.dto.repairing

import com.makarytskyi.rentcar.model.Repairing
import java.util.Date

data class RepairingResponse(
    val id: String,
    val carId: String,
    val date: Date?,
    val price: Int?,
    val status: Repairing.RepairingStatus?,
)
