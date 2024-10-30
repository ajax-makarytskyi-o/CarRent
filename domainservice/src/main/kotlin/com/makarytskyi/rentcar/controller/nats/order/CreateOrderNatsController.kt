package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.mapper.toCreateResponse
import com.makarytskyi.rentcar.mapper.toDto
import com.makarytskyi.rentcar.service.OrderService
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CreateOrderNatsController(
    override val connection: Connection,
    private val orderService: OrderService
) : NatsController<CreateOrderRequest, CreateOrderResponse> {
    override val subject = CREATE
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<CreateOrderRequest> = CreateOrderRequest.parser()
    override val defaultResponse: CreateOrderResponse = CreateOrderResponse.getDefaultInstance()

    override fun handle(request: CreateOrderRequest): Mono<CreateOrderResponse> =
        orderService.create(request.toDto())
            .map { it.toCreateResponse() }
            .onErrorResume { it.toCreateResponse() }

    companion object {
        const val QUEUE_GROUP = "create_order"
    }
}
