package com.makarytskyi.rentcar.kafka

import com.makarytskyi.internalapi.commonmodels.repairing.Repairing
import com.makarytskyi.internalapi.subject.KafkaTopic
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class CreateRepairingKafkaProducer(
    private val kafkaSender: KafkaSender<String, ByteArray>
) {

    fun sendCreateRepairing(repairing: Repairing): Mono<Unit> =
        kafkaSender.send(
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