package com.makarytskyi.rentcar.order.infrastructure.kafka

import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.internalapi.topic.KafkaTopic
import com.makarytskyi.rentcar.order.application.port.output.CreateOrderProducerOutputPort
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.publisher.KafkaPublisher

@Component
class CreateOrderKafkaProducer(
    private val publisher: KafkaPublisher,
) : CreateOrderProducerOutputPort {

    override fun sendCreateOrder(order: Order): Mono<Unit> =
        publisher.publish(
            KafkaTopic.Order.ORDER_CREATE,
            order.userId,
            order
        ).then(Unit.toMono())
}
