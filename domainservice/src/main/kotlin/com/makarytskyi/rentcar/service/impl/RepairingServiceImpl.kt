package com.makarytskyi.rentcar.service.impl

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.topic.KafkaTopic
import com.makarytskyi.rentcar.annotation.InvocationTracker
import com.makarytskyi.rentcar.dto.repairing.AggregatedRepairingResponse
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.kafka.CreateRepairingKafkaProducer
import com.makarytskyi.rentcar.mapper.toProto
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.RepairingRepository
import com.makarytskyi.rentcar.service.RepairingService
import java.util.Date
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@InvocationTracker
@Service
internal class RepairingServiceImpl(
    private val repairingRepository: RepairingRepository,
    private val carRepository: CarRepository,
    private val repairingKafkaProducer: CreateRepairingKafkaProducer,
) : RepairingService {

    override fun findFullAll(page: Int, size: Int): Flux<AggregatedRepairingResponse> =
        repairingRepository.findFullAll(page, size).map { AggregatedRepairingResponse.from(it) }

    override fun create(repairingRequest: CreateRepairingRequest): Mono<RepairingResponse> =
        repairingRequest.toMono()
            .doOnNext { validateDate(it.date) }
            .flatMap { validateCarExists(repairingRequest.carId) }
            .flatMap { repairingRepository.create(CreateRepairingRequest.toEntity(repairingRequest)) }
            .doOnNext {
                repairingKafkaProducer.sendCreateRepairing(it.toProto())
                    .doOnError { error ->
                        log.error(
                            "Error happened during sending message to ${KafkaTopic.REPAIRING_CREATE} topic",
                            error
                        )
                    }
                    .subscribe()
            }
            .map { RepairingResponse.from(it) }

    override fun getFullById(id: String): Mono<AggregatedRepairingResponse> = repairingRepository.findFullById(id)
        .switchIfEmpty { Mono.error(NotFoundException("Repairing with id $id is not found")) }
        .map { AggregatedRepairingResponse.from(it) }

    override fun deleteById(id: String): Mono<Unit> = repairingRepository.deleteById(id)

    override fun patch(id: String, repairingRequest: UpdateRepairingRequest): Mono<RepairingResponse> =
        repairingRepository.patch(id, UpdateRepairingRequest.toPatch(repairingRequest))
            .switchIfEmpty { Mono.error(NotFoundException("Repairing with id $id is not found")) }
            .map { RepairingResponse.from(it) }

    override fun findByStatus(status: MongoRepairing.RepairingStatus): Flux<RepairingResponse> =
        repairingRepository.findByStatus(status).map { RepairingResponse.from(it) }

    override fun findByCarId(carId: String): Flux<RepairingResponse> =
        repairingRepository.findByCarId(carId).map { RepairingResponse.from(it) }

    override fun findByStatusAndCar(status: MongoRepairing.RepairingStatus, carId: String): Flux<RepairingResponse> =
        repairingRepository.findByStatusAndCarId(status, carId).map { RepairingResponse.from(it) }

    private fun validateCarExists(carId: String): Mono<MongoCar> = carRepository.findById(carId)
        .switchIfEmpty { Mono.error(NotFoundException("Car in repairing with id $carId is not found")) }

    private fun validateDate(date: Date?) {
        require(date?.after(Date()) == true) { "Date must be in future" }
    }

    companion object {
        val log = LoggerFactory.getLogger(RepairingServiceImpl::class.java)
    }
}
