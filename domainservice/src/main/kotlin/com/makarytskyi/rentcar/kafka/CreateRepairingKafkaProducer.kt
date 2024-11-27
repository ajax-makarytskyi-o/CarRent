package com.makarytskyi.rentcar.kafka

import com.makarytskyi.commonmodels.repairing.Repairing
import com.makarytskyi.internalapi.topic.KafkaTopic
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.publisher.KafkaPublisher
import systems.ajax.kafka.publisher.options.KafkaPublisherOptions

@Component
class CreateRepairingKafkaProducer(
    private val publisher: KafkaPublisher,
) {

    fun sendCreateRepairing(repairing: Repairing): Mono<Unit> =
        publisher.publish(
            KafkaTopic.Repairing.REPAIRING_CREATE,
            repairing.carId,
            repairing,
            KafkaPublisherOptions()
        ).then(Unit.toMono())
}
