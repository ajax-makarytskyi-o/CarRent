package com.makarytskyi.rentcar.repairing.infrastructure.kafka

import com.makarytskyi.commonmodels.repairing.Repairing
import com.makarytskyi.internalapi.topic.KafkaTopic
import com.makarytskyi.rentcar.repairing.application.port.output.CreateRepairingProducerOutputPort
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.publisher.KafkaPublisher

@Component
class CreateRepairingKafkaProducer(
    private val publisher: KafkaPublisher,
) : CreateRepairingProducerOutputPort {

    override fun sendCreateRepairing(repairing: Repairing): Mono<Unit> =
        publisher.publish(
            KafkaTopic.Repairing.REPAIRING_CREATE,
            repairing.carId,
            repairing
        ).then(Unit.toMono())
}
