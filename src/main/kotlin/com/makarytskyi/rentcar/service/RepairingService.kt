package com.makarytskyi.rentcar.service

import com.makarytskyi.rentcar.dto.repairing.AggregatedRepairingResponse
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.model.MongoRepairing
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

internal interface RepairingService {

    fun findFullAll(page: Int, size: Int): Flux<AggregatedRepairingResponse>

    fun create(repairingRequest: CreateRepairingRequest): Mono<RepairingResponse>

    fun getFullById(id: String): Mono<AggregatedRepairingResponse>

    fun deleteById(id: String): Mono<Unit>

    fun patch(id: String, repairingRequest: UpdateRepairingRequest): Mono<RepairingResponse>

    fun findByStatus(status: MongoRepairing.RepairingStatus): Flux<RepairingResponse>

    fun findByCarId(carId: String): Flux<RepairingResponse>

    fun findByStatusAndCar(status: MongoRepairing.RepairingStatus, carId: String): Flux<RepairingResponse>
}
