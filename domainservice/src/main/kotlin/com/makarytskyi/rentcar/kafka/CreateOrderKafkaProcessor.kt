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
import systems.ajax.kafka.handler.options.KafkaHandlerOptions
import systems.ajax.kafka.handler.options.RetryStrategy
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

    override val options: KafkaHandlerOptions<Order> = KafkaHandlerOptions<Order>()
        .retry(
            RetryStrategy.InPlace(
                algorithm = RetryStrategy.RetryAlgorithm.Exponential(
                    RETRY_TIMES,
                    Duration.ofSeconds(RETRY_DURATION_SECONDS)
                ),
            )
        )

    override fun handle(kafkaEvent: KafkaEvent<Order>): Mono<Unit> =
        kafkaEvent.toMono()
            .flatMap { sendCreateEvent(it.data) }
            .doOnSuccess { kafkaEvent.ack() }

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

    companion object {
        const val RETRY_TIMES = 100
        const val RETRY_DURATION_SECONDS: Long = 1

        const val GROUP_ID_ORDER = "group-rentcar-order"
    }
}
