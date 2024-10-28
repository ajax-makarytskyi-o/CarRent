package com.makarytskyi.gateway.config

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class NatsClient(private val natsConnection: Connection) {
    fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> request(
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
