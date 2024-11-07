package com.makarytskyi.rentcar.controller

import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import com.makarytskyi.rentcar.repository.ContainerBase
import io.nats.client.Connection
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractOrderNatsControllerTest : ContainerBase {
    @Autowired
    private lateinit var connection: Connection

    fun <RequestT : GeneratedMessage, ResponseT : GeneratedMessage> sendRequest(
        subject: String,
        request: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = connection.request(subject, request.toByteArray())
        return parser.parseFrom(response.get().data)
    }
}
