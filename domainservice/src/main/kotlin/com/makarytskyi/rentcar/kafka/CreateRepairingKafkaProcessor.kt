package com.makarytskyi.rentcar.kafka

import com.makarytskyi.internalapi.commonmodels.repairing.Repairing
import com.makarytskyi.rentcar.mapper.OrderMapper.toNotification
import com.makarytskyi.rentcar.service.OrderService
import com.makarytskyi.rentcar.util.timestampToDate
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.core.publisher.toMono

@Component
class CreateRepairingKafkaProcessor(
    private val createRepairingKafkaReceiver: KafkaReceiver<String, ByteArray>,
    private val userNotificationKafkaProducer: UserNotificationKafkaProducer,
    private val orderService: OrderService,
) {

    @EventListener(ApplicationReadyEvent::class)
    fun consume() {
        createRepairingKafkaReceiver.receive()
            .flatMap { record ->
                Repairing.parser().parseFrom(record.value()).toMono()
                    .flatMap {
                        orderService.findOrderByCarAndDate(it.carId, timestampToDate(it.date))
                            .doOnNext { order -> orderService.deleteById(order.id) }
                            .flatMap { order -> userNotificationKafkaProducer.sendNotification(order.toNotification()) }
                    }
                    .doFinally { record.receiverOffset().acknowledge() }
            }
            .subscribe()
    }
}
