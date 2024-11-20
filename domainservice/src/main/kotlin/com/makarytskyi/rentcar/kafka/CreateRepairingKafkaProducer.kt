package com.makarytskyi.rentcar.kafka

import com.makarytskyi.commonmodels.repairing.Repairing
import com.makarytskyi.internalapi.topic.KafkaTopic
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class CreateRepairingKafkaProducer(
    private val createRepairingKafkaSender: KafkaSender<String, ByteArray>
) {

    fun sendCreateRepairing(repairing: Repairing): Mono<Unit> =
        createRepairingKafkaSender.send(
            SenderRecord.create(
                ProducerRecord(
                    KafkaTopic.REPAIRING_CREATE,
                    repairing.carId,
                    repairing.toByteArray()
                ),
                null
            ).toMono()
        ).then(Unit.toMono())
}
