package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.rentcar.mapper.OrderMapper.toCreateResponse
import com.makarytskyi.rentcar.mapper.OrderMapper.toDto
import com.makarytskyi.rentcar.mapper.toCreateFailureResponse
import com.makarytskyi.rentcar.service.OrderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class CreateOrderNatsController(
    val orderService: OrderService,
) : ProtoNatsMessageHandler<CreateOrderRequest, CreateOrderResponse> {
    override val subject: String = CREATE
    override val queue: String = QUEUE_GROUP
    override val log: Logger = LoggerFactory.getLogger(CreateOrderNatsController::class.java)
    override val parser: Parser<CreateOrderRequest> = CreateOrderRequest.parser()

    override fun doHandle(inMsg: CreateOrderRequest): Mono<CreateOrderResponse> =
        orderService.create(inMsg.toDto())
            .map { it.toCreateResponse() }
            .onErrorResume { it.toCreateFailureResponse().toMono() }

    override fun doOnUnexpectedError(inMsg: CreateOrderRequest?, e: Exception): Mono<CreateOrderResponse> =
        CreateOrderResponse.newBuilder()
            .apply {
                failureBuilder.message = e.message
            }.build().toMono()

    companion object {
        const val QUEUE_GROUP = "create_order"
    }
}
