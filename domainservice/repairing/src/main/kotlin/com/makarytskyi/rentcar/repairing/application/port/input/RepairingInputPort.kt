package com.makarytskyi.rentcar.repairing.application.port.input

import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.domain.patch.DomainRepairingPatch
import com.makarytskyi.rentcar.repairing.domain.projection.AggregatedDomainRepairing
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RepairingInputPort {
    fun findFullAll(page: Int, size: Int): Flux<AggregatedDomainRepairing>

    fun create(repairingRequest: DomainRepairing): Mono<DomainRepairing>

    fun getFullById(id: String): Mono<AggregatedDomainRepairing>

    fun deleteById(id: String): Mono<Unit>

    fun patch(id: String, repairingRequest: DomainRepairingPatch): Mono<DomainRepairing>

    fun findByStatus(status: DomainRepairing.RepairingStatus): Flux<DomainRepairing>

    fun findByCarId(carId: String): Flux<DomainRepairing>

    fun findByStatusAndCar(status: DomainRepairing.RepairingStatus, carId: String): Flux<DomainRepairing>

}
