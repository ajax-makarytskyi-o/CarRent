package com.makarytskyi.rentcar.order.infrastructure.nats

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.rentcar.order.application.port.input.OrderInputPort
import com.makarytskyi.rentcar.order.infrastructure.nats.mapper.OrderProtoMapper.toCreateFailureResponse
import com.makarytskyi.rentcar.order.infrastructure.nats.mapper.OrderProtoMapper.toCreateResponse
import com.makarytskyi.rentcar.order.infrastructure.nats.mapper.OrderProtoMapper.toDomain
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class CreateOrderNatsController(
    val orderInputPort: OrderInputPort,
) : ProtoNatsMessageHandler<CreateOrderRequest, CreateOrderResponse> {
    override val subject: String = CREATE
    override val queue: String = QUEUE_GROUP
    override val log: Logger = LoggerFactory.getLogger(CreateOrderNatsController::class.java)
    override val parser: Parser<CreateOrderRequest> = CreateOrderRequest.parser()

    override fun doHandle(inMsg: CreateOrderRequest): Mono<CreateOrderResponse> =
        orderInputPort.create(inMsg.toDomain())
            .map { it.toCreateResponse() }
            .onErrorResume { it.toCreateFailureResponse().toMono() }

    override fun doOnUnexpectedError(inMsg: CreateOrderRequest?, e: Exception): Mono<CreateOrderResponse> =
        e.toCreateFailureResponse().toMono()

    companion object {
        const val QUEUE_GROUP = "create_order"
    }
}
