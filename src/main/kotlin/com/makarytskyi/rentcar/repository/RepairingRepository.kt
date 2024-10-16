package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.patch.MongoRepairingPatch
import com.makarytskyi.rentcar.model.projection.AggregatedMongoRepairing
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
internal interface RepairingRepository {

    fun create(mongoRepairing: MongoRepairing): Mono<MongoRepairing>

    fun findFullById(id: String): Mono<AggregatedMongoRepairing>

    fun findFullAll(page: Int, size: Int): Flux<AggregatedMongoRepairing>

    fun deleteById(id: String): Mono<Unit>

    fun patch(id: String, repairingPatch: MongoRepairingPatch): Mono<MongoRepairing>

    fun findByStatus(status: MongoRepairing.RepairingStatus): Flux<MongoRepairing>

    fun findByCarId(carId: String): Flux<MongoRepairing>

    fun findByStatusAndCarId(status: MongoRepairing.RepairingStatus, carId: String): Flux<MongoRepairing>
}
