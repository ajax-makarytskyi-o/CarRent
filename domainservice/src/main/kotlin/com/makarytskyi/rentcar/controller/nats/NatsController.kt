package com.makarytskyi.rentcar.controller.nats

import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import io.nats.client.Connection
import reactor.core.publisher.Mono

interface NatsController<RequestT : GeneratedMessage, ResponseT : GeneratedMessage> {
    val subject: String
    val connection: Connection
    val queueGroup: String
    val defaultResponse: ResponseT
    val parser: Parser<RequestT>
    fun handle(request: RequestT): Mono<ResponseT>
}
