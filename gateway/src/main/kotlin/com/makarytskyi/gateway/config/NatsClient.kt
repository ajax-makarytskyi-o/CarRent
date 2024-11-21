package com.makarytskyi.gateway.config

import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.grpcapi.input.reqreply.order.StreamCreatedOrdersByCar.StreamCreatedOrdersByUserIdResponse
import com.makarytskyi.internalapi.subject.NatsSubject
import io.nats.client.Connection
import io.nats.client.Dispatcher
import java.time.Duration
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class NatsClient(
    private val natsConnection: Connection,
    @Value("\${nats.timeout}")
    private val timeoutDuration: Long,
    private val dispatcher: Dispatcher,
) {
    fun <RequestT : GeneratedMessage, ResponseT : GeneratedMessage> request(
        subject: String,
        data: RequestT,
        parser: Parser<ResponseT>,
    ): Mono<ResponseT> = Mono.fromFuture {
        natsConnection.requestWithTimeout(
            subject,
            data.toByteArray(),
            Duration.ofSeconds(timeoutDuration)
        )
    }.map { parser.parseFrom(it.data) }

    fun streamCreatedOrdersByCarId(userId: String): Flux<StreamCreatedOrdersByUserIdResponse> {
        val natsSubject = NatsSubject.Order.createOrderOnCar(userId)
        return Flux.create { sink ->
            dispatcher.subscribe(natsSubject) {
                val order = Order.parser().parseFrom(it.data)
                val response = StreamCreatedOrdersByUserIdResponse.newBuilder().also { it.order = order }.build()
                sink.next(response)
            }
        }
            .doFinally {
                dispatcher.unsubscribe(natsSubject)
            }
    }
}
