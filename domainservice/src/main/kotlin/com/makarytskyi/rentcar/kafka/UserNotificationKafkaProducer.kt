package com.makarytskyi.rentcar.kafka

import com.makarytskyi.internalapi.commonmodels.order.OrderCancellationUserNotification
import com.makarytskyi.internalapi.subject.KafkaTopic
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class UserNotificationKafkaProducer(
    private val kafkaSender: KafkaSender<String, ByteArray>
) {

    fun sendNotification(notification: OrderCancellationUserNotification): Mono<Unit> =
        kafkaSender.send(
            SenderRecord.create(
                ProducerRecord(
                    KafkaTopic.NOTIFICATION,
                    notification.userId,
                    notification.toByteArray()
                ),
                null
            ).toMono()
        ).then(Unit.toMono())
}
