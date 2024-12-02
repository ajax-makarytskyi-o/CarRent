package com.makarytskyi.rentcar.repairing.application.port.output

import com.makarytskyi.commonmodels.repairing.Repairing
import reactor.core.publisher.Mono

interface CreateRepairingProducerOutputPort {
    fun sendCreateRepairing(repairing: Repairing): Mono<Unit>
}
