package com.makarytskyi.gateway.config

import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class NatsClient(private val natsConnection: Connection) {
    fun <RequestT : GeneratedMessage, ResponseT : GeneratedMessage> request(
        subject: String,
        data: RequestT,
        parser: Parser<ResponseT>
    ): Mono<ResponseT> = Mono.fromFuture {
        natsConnection.request(
            subject,
            data.toByteArray()
        )
    }.map { parser.parseFrom(it.data) }
}
