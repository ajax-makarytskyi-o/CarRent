package com.makarytskyi.rentcar.kafka

import com.google.protobuf.Parser
import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.internalapi.subject.NatsSubject
import com.makarytskyi.internalapi.topic.KafkaTopic
import io.nats.client.Connection
import java.time.Duration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.util.retry.Retry
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.kafka.handler.KafkaHandler
import systems.ajax.kafka.handler.subscription.topic.TopicSingle

@Component
class CreateOrderKafkaProcessor : KafkaHandler<Order, TopicSingle> {
    @Autowired
    private lateinit var natsConnection: Connection

    override val subscriptionTopics: TopicSingle = TopicSingle(KafkaTopic.Order.ORDER_CREATE)

    override val groupId: String = GROUP_ID_ORDER

    override val parser: Parser<Order> = Order.parser()

    override fun handle(kafkaEvent: KafkaEvent<Order>): Mono<Unit> =
        kafkaEvent.toMono()
            .flatMap { sendCreateEvent(it.data).retryWhen(retryOnNatsError()) }
            .doFinally { kafkaEvent.ack() }
            .then(Unit.toMono())

    private fun sendCreateEvent(order: Order): Mono<Unit> {
        return Mono.defer { natsConnection.status.toMono() }
            .flatMap { status ->
                if (status == Connection.Status.CONNECTED) {
                    natsConnection.publish(
                        NatsSubject.Order.createOrderOnCar(order.userId),
                        order.toByteArray()
                    ).toMono()
                } else {
                    IllegalStateException("NATS is unavailable").toMono()
                }
            }
    }

    private fun retryOnNatsError(): Retry = Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2))
        .filter { it is IllegalStateException }

    companion object {
        const val GROUP_ID_ORDER = "group-rentcar-order"
    }
}
