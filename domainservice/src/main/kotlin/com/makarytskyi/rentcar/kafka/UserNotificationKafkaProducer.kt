package com.makarytskyi.rentcar.kafka

import com.makarytskyi.commonmodels.order.OrderCancellationNotification
import com.makarytskyi.internalapi.topic.KafkaTopic
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class UserNotificationKafkaProducer(
    private val userNotificationKafkaSender: KafkaSender<String, ByteArray>
) {

    fun sendNotification(notification: OrderCancellationNotification): Mono<Unit> =
        userNotificationKafkaSender.send(
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
