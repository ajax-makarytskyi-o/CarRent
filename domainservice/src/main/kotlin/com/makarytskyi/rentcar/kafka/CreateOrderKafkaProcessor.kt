package com.makarytskyi.rentcar.kafka

import com.google.protobuf.Parser
import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.internalapi.subject.NatsSubject
import com.makarytskyi.internalapi.topic.KafkaTopic
import com.makarytskyi.rentcar.config.NatsListener
import java.time.Duration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.util.retry.Retry
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.kafka.handler.KafkaHandler
import systems.ajax.kafka.handler.subscription.topic.TopicSingle
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@Component
class CreateOrderKafkaProcessor : KafkaHandler<Order, TopicSingle> {
    @Autowired
    private lateinit var natsPublisher: NatsMessagePublisher

    @Autowired
    private lateinit var connectionListener: NatsListener

    override val subscriptionTopics: TopicSingle = TopicSingle(KafkaTopic.Order.ORDER_CREATE)

    override val groupId: String = GROUP_ID_ORDER

    override val parser: Parser<Order> = Order.parser()

    override fun handle(kafkaEvent: KafkaEvent<Order>): Mono<Unit> =
        kafkaEvent.toMono()
            .flatMap { sendCreateEvent(it.data).retryWhen(retryOnNatsError()) }
            .doFinally { kafkaEvent.ack() }
            .then(Unit.toMono())

    private fun sendCreateEvent(order: Order): Mono<Unit> {
        return order.toMono()
            .flatMap {
                if (connectionListener.isConnected())
                    natsPublisher.publish(
                        NatsSubject.Order.createOrderOnCar(order.userId),
                        order
                    )
                else
                    IllegalStateException("NATS connection is closed").toMono()
            }
    }

    private fun retryOnNatsError(): Retry = Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2))
        .filter { it is IllegalStateException && !connectionListener.isConnected() }

    companion object {
        const val GROUP_ID_ORDER = "group-rentcar-order"
    }
}
