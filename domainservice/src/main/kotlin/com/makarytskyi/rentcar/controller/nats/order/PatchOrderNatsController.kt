package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.UPDATE
import com.makarytskyi.rentcar.mapper.OrderMapper.toDto
import com.makarytskyi.rentcar.mapper.OrderMapper.toPatchResponse
import com.makarytskyi.rentcar.mapper.toPatchFailureResponse
import com.makarytskyi.rentcar.service.OrderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class PatchOrderNatsController(
    private val orderService: OrderService,
) : ProtoNatsMessageHandler<UpdateOrderRequest, UpdateOrderResponse> {
    override val subject: String = UPDATE
    override val queue: String = QUEUE_GROUP
    override val parser: Parser<UpdateOrderRequest> = UpdateOrderRequest.parser()
    override val log: Logger = LoggerFactory.getLogger(PatchOrderNatsController::class.java)

    override fun doHandle(inMsg: UpdateOrderRequest): Mono<UpdateOrderResponse> =
        orderService.patch(inMsg.id, inMsg.update.toDto())
            .map { it.toPatchResponse() }
            .onErrorResume { it.toPatchFailureResponse().toMono() }

    override fun doOnUnexpectedError(inMsg: UpdateOrderRequest?, e: Exception): Mono<UpdateOrderResponse> =
        e.toPatchFailureResponse().toMono()

    companion object {
        const val QUEUE_GROUP = "patch_order"
    }
}
