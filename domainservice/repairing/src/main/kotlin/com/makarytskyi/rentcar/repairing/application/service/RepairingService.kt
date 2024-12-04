package com.makarytskyi.rentcar.repairing.application.service

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.topic.KafkaTopic
import com.makarytskyi.rentcar.car.application.port.output.CarRepositoryOutputPort
import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.common.annotation.InvocationTracker
import com.makarytskyi.rentcar.repairing.application.mapper.toProto
import com.makarytskyi.rentcar.repairing.application.port.input.RepairingServiceInputPort
import com.makarytskyi.rentcar.repairing.application.port.output.CreateRepairingProducerOutputPort
import com.makarytskyi.rentcar.repairing.application.port.output.RepairingRepositoryOutputPort
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.domain.create.CreateRepairing
import com.makarytskyi.rentcar.repairing.domain.patch.PatchRepairing
import com.makarytskyi.rentcar.repairing.domain.projection.AggregatedDomainRepairing
import java.util.Date
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@InvocationTracker
@Service
class RepairingService(
    private val repairingOutputPort: RepairingRepositoryOutputPort,
    private val carOutputPort: CarRepositoryOutputPort,
    private val createRepairingProducerOutputPort: CreateRepairingProducerOutputPort,
) : RepairingServiceInputPort {

    override fun findFullAll(page: Int, size: Int): Flux<AggregatedDomainRepairing> =
        repairingOutputPort.findFullAll(page, size)

    override fun create(repairing: CreateRepairing): Mono<DomainRepairing> =
        repairing.toMono()
            .doOnNext { validateDate(it.date) }
            .flatMap { validateCarExists(repairing.carId) }
            .flatMap { repairingOutputPort.create(repairing) }
            .doOnNext {
                createRepairingProducerOutputPort.sendCreateRepairing(it.toProto())
                    .doOnError { error ->
                        log.atError()
                            .setMessage("Error happened during sending message to {} topic")
                            .addArgument(KafkaTopic.Repairing.REPAIRING_CREATE)
                            .setCause(error)
                            .log()
                    }
                    .subscribe()
            }

    override fun getFullById(id: String): Mono<AggregatedDomainRepairing> = repairingOutputPort.findFullById(id)
        .switchIfEmpty { Mono.error(NotFoundException("Repairing with id $id is not found")) }

    override fun deleteById(id: String): Mono<Unit> = repairingOutputPort.deleteById(id)

    override fun patch(id: String, patch: PatchRepairing): Mono<DomainRepairing> =
        repairingOutputPort.findById(id)
            .flatMap { repairingOutputPort.patch(id, it.fromPatch(patch)) }
            .switchIfEmpty { Mono.error(NotFoundException("Repairing with id $id is not found")) }

    override fun findByStatus(status: DomainRepairing.RepairingStatus): Flux<DomainRepairing> =
        repairingOutputPort.findByStatus(status)

    override fun findByCarId(carId: String): Flux<DomainRepairing> =
        repairingOutputPort.findByCarId(carId)

    override fun findByStatusAndCar(status: DomainRepairing.RepairingStatus, carId: String): Flux<DomainRepairing> =
        repairingOutputPort.findByStatusAndCarId(status, carId)

    private fun validateCarExists(carId: String): Mono<DomainCar> = carOutputPort.findById(carId)
        .switchIfEmpty { Mono.error(NotFoundException("Car in repairing with id $carId is not found")) }

    private fun validateDate(date: Date?) {
        require(date?.after(Date()) == true) { "Date must be in future" }
    }

    companion object {
        val log = LoggerFactory.getLogger(RepairingService::class.java)
    }
}
