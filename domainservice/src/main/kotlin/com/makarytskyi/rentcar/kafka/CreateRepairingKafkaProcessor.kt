package com.makarytskyi.rentcar.kafka

import com.makarytskyi.internalapi.commonmodels.repairing.Repairing
import com.makarytskyi.rentcar.mapper.OrderMapper.toNotification
import com.makarytskyi.rentcar.service.OrderService
import com.makarytskyi.rentcar.util.timestampToDate
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver

@Component
class CreateRepairingKafkaProcessor(
    val kafkaReceiver: KafkaReceiver<String, ByteArray>,
    val userNotificationKafkaProducer: UserNotificationKafkaProducer,
    val orderService: OrderService
) {

    @PostConstruct
    fun consume() {
        kafkaReceiver.receive()
            .flatMap { record ->
                val repairing = Repairing.parser().parseFrom(record.value())
                orderService.findOrderByDateAndCar(timestampToDate(repairing.date), repairing.carId)
                    .doOnNext { orderService.deleteById(repairing.id) }
                    .flatMap { userNotificationKafkaProducer.sendNotification(it.toNotification()) }
                    .doFinally { record.receiverOffset().acknowledge() }
            }
            .subscribe()
    }
}
