package com.makarytskyi.rentcar.order.infrastructure.nats

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.GET_BY_ID
import com.makarytskyi.rentcar.order.application.port.input.OrderServiceInputPort
import com.makarytskyi.rentcar.order.infrastructure.nats.mapper.OrderProtoMapper.toGetByIdFailureResponse
import com.makarytskyi.rentcar.order.infrastructure.nats.mapper.OrderProtoMapper.toGetByIdResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class GetByIdOrderNatsController(
    private val orderInputPort: OrderServiceInputPort,
) : ProtoNatsMessageHandler<GetByIdOrderRequest, GetByIdOrderResponse> {
    override val subject: String = GET_BY_ID
    override val queue: String = QUEUE_GROUP
    override val parser: Parser<GetByIdOrderRequest> = GetByIdOrderRequest.parser()
    override val log: Logger = LoggerFactory.getLogger(GetByIdOrderNatsController::class.java)

    override fun doHandle(inMsg: GetByIdOrderRequest): Mono<GetByIdOrderResponse> =
        orderInputPort.getById(inMsg.id)
            .map { it.toGetByIdResponse() }
            .onErrorResume { it.toGetByIdFailureResponse().toMono() }

    override fun doOnUnexpectedError(inMsg: GetByIdOrderRequest?, e: Exception): Mono<GetByIdOrderResponse> =
        e.toGetByIdFailureResponse().toMono()

    companion object {
        const val QUEUE_GROUP = "get_by_id_order"
    }
}
