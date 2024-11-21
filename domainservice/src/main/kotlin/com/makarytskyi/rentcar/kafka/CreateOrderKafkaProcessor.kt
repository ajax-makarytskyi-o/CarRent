package com.makarytskyi.rentcar.kafka

import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.internalapi.subject.NatsSubject
import io.nats.client.Connection
import java.time.Duration
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.core.publisher.toMono
import reactor.util.retry.Retry

@Component
class CreateOrderKafkaProcessor(
    private val createOrderKafkaReceiver: KafkaReceiver<String, ByteArray>,
    private val natsConnection: Connection,
) {

    @EventListener(ApplicationReadyEvent::class)
    fun consume() {
        createOrderKafkaReceiver.receive()
            .concatMap { record ->
                record.value().toMono()
                    .map { Order.parser().parseFrom(it) }
                    .onErrorResume {
                        log.atError()
                            .setMessage("Error happened during parsing: {}")
                            .addArgument(it.message)
                            .setCause(it)
                            .log()

                        Mono.empty()
                    }
                    .flatMap { sendCreateEvent(it) }
                    .retryWhen(retryOnNatsConnection())
                    .doFinally {
                        record.receiverOffset().acknowledge()
                    }
            }
            .subscribe()
    }

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

    private fun retryOnNatsConnection(): Retry {
        return Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2))
            .filter { throwable -> throwable is IllegalStateException }
    }

    companion object {
        val log = LoggerFactory.getLogger(CreateOrderKafkaProcessor::class.java)
    }
}
