package com.makarytskyi.rentcar.repairing.application.port.output

import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.domain.create.CreateRepairing
import com.makarytskyi.rentcar.repairing.domain.projection.AggregatedDomainRepairing
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RepairingRepositoryOutputPort {
    fun create(repairing: CreateRepairing): Mono<DomainRepairing>

    fun findFullById(id: String): Mono<AggregatedDomainRepairing>

    fun findById(id: String): Mono<DomainRepairing>

    fun findFullAll(page: Int, size: Int): Flux<AggregatedDomainRepairing>

    fun deleteById(id: String): Mono<Unit>

    fun patch(id: String, patch: DomainRepairing): Mono<DomainRepairing>

    fun findByStatus(status: DomainRepairing.RepairingStatus): Flux<DomainRepairing>

    fun findByCarId(carId: String): Flux<DomainRepairing>

    fun findByStatusAndCarId(status: DomainRepairing.RepairingStatus, carId: String): Flux<DomainRepairing>
}
