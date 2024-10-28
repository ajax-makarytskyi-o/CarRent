package com.makarytskyi.rentcar.model.patch

import com.makarytskyi.rentcar.model.MongoRepairing.RepairingStatus
import java.math.BigDecimal

data class MongoRepairingPatch(
    val price: BigDecimal?,
    val status: RepairingStatus?,
)
