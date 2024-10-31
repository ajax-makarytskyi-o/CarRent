package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.mapper.OrderMapper.toCreateResponse
import com.makarytskyi.rentcar.mapper.OrderMapper.toDto
import com.makarytskyi.rentcar.mapper.toCreateFailureResponse
import com.makarytskyi.rentcar.service.OrderService
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CreateOrderNatsController(
    override val connection: Connection,
    private val orderService: OrderService
) : NatsController<CreateOrderRequest, CreateOrderResponse> {
    override val subject = CREATE
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<CreateOrderRequest> = CreateOrderRequest.parser()
    override val defaultResponse: CreateOrderResponse = CreateOrderResponse.newBuilder()
        .also { it.failureBuilder.message = "Error happend during parsing." }.build()

    override fun handle(request: CreateOrderRequest): Mono<CreateOrderResponse> =
        orderService.create(request.toDto())
            .map { it.toCreateResponse() }
            .onErrorResume { it.toCreateFailureResponse().toMono() }

    companion object {
        const val QUEUE_GROUP = "create_order"
    }
}
