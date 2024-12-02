package com.makarytskyi.rentcar.repairing.domain.patch

import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import java.math.BigDecimal

data class DomainRepairingPatch(
    val price: BigDecimal?,
    val status: DomainRepairing.RepairingStatus?,
)
