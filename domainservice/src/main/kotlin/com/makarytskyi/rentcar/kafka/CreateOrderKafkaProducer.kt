package com.makarytskyi.rentcar.kafka

import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.internalapi.topic.KafkaTopic
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class CreateOrderKafkaProducer(
    private val createOrderKafkaSender: KafkaSender<String, ByteArray>,
) {

    fun sendCreateRepairing(order: Order): Mono<Unit> =
        createOrderKafkaSender.send(
            SenderRecord.create(
                ProducerRecord(
                    KafkaTopic.Order.ORDER_CREATE,
                    order.userId,
                    order.toByteArray()
                ),
                null
            ).toMono()
        ).then(Unit.toMono())
}
