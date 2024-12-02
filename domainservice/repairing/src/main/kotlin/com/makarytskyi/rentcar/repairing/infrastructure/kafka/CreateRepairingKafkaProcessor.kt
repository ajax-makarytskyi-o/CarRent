package com.makarytskyi.rentcar.repairing.infrastructure.kafka

import com.google.protobuf.Parser
import com.makarytskyi.commonmodels.repairing.Repairing
import com.makarytskyi.internalapi.topic.KafkaTopic
import com.makarytskyi.rentcar.common.util.Utils.timestampToDate
import com.makarytskyi.rentcar.order.application.port.input.OrderInputPort
import com.makarytskyi.rentcar.order.infrastructure.kafka.toNotification
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.kafka.handler.KafkaHandler
import systems.ajax.kafka.handler.subscription.topic.TopicSingle

@Component
class CreateRepairingKafkaProcessor(
    private val userNotificationKafkaProducer: UserNotificationKafkaProducer,
    private val orderService: OrderInputPort,
) : KafkaHandler<Repairing, TopicSingle> {

    override val subscriptionTopics: TopicSingle = TopicSingle(KafkaTopic.Repairing.REPAIRING_CREATE)

    override val groupId: String = GROUP_ID_REPAIRING

    override val parser: Parser<Repairing> = Repairing.parser()

    override fun handle(kafkaEvent: KafkaEvent<Repairing>): Mono<Unit> =
        kafkaEvent.toMono()
            .flatMap {
                orderService.findOrderByCarAndDate(it.data.carId, timestampToDate(it.data.date))
                    .doOnNext { order -> order.id?.let { orderId -> orderService.deleteById(orderId) } }
                    .flatMap { order -> userNotificationKafkaProducer.sendNotification(order.toNotification()) }
            }
            .doFinally { kafkaEvent.ack() }
            .then(Unit.toMono())

    companion object {
        const val GROUP_ID_REPAIRING = "group-rentcar-repairing"
    }
}
