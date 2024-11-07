package com.makarytskyi.gateway.config

import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import io.nats.client.Connection
import java.time.Duration
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class NatsClient(
    private val natsConnection: Connection,
    @Value("\${nats.timeout}")
    private val timeoutDuration: Long
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
}
