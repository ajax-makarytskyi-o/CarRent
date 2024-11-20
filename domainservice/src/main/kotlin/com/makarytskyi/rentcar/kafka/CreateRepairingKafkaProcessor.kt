package com.makarytskyi.rentcar.kafka

import com.makarytskyi.commonmodels.repairing.Repairing
import com.makarytskyi.rentcar.mapper.OrderMapper.toNotification
import com.makarytskyi.rentcar.service.OrderService
import com.makarytskyi.rentcar.util.Utils.timestampToDate
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
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
                record.value().toMono()
                    .map { Repairing.parser().parseFrom(it) }
                    .onErrorResume {
                        log.atError()
                            .setMessage("Error happened during parsing: {}")
                            .addArgument(it.message)
                            .setCause(it)
                            .log()

                        Mono.empty()
                    }
                    .flatMap {
                        orderService.findOrderByCarAndDate(it.carId, timestampToDate(it.date))
                            .doOnNext { order -> orderService.deleteById(order.id) }
                            .flatMap { order -> userNotificationKafkaProducer.sendNotification(order.toNotification()) }
                    }
                    .doFinally { record.receiverOffset().acknowledge() }
            }
            .subscribe()
    }

    companion object {
        val log = LoggerFactory.getLogger(CreateRepairingKafkaProcessor::class.java)
    }
}
