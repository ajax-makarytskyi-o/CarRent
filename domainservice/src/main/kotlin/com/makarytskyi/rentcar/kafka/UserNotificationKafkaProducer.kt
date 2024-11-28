package com.makarytskyi.rentcar.kafka

import com.makarytskyi.commonmodels.order.OrderCancellationNotification
import com.makarytskyi.internalapi.topic.KafkaTopic
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.publisher.KafkaPublisher

@Component
class UserNotificationKafkaProducer(
    private val publisher: KafkaPublisher,
) {

    fun sendNotification(notification: OrderCancellationNotification): Mono<Unit> =
        publisher.publish(
            KafkaTopic.User.NOTIFICATION,
            notification.userId,
            notification
        ).then(Unit.toMono())
}
