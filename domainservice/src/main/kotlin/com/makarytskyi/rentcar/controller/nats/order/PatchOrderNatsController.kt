package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.UPDATE
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.mapper.OrderMapper.toDto
import com.makarytskyi.rentcar.mapper.OrderMapper.toPatchResponse
import com.makarytskyi.rentcar.mapper.toPatchFailureResponse
import com.makarytskyi.rentcar.service.OrderService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class PatchOrderNatsController(
    private val orderService: OrderService
) : NatsController<UpdateOrderRequest, UpdateOrderResponse> {
    override val subject = UPDATE
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<UpdateOrderRequest> = UpdateOrderRequest.parser()
    override val defaultResponse: UpdateOrderResponse = UpdateOrderResponse.getDefaultInstance()

    override fun handle(request: UpdateOrderRequest): Mono<UpdateOrderResponse> =
        orderService.patch(request.id, request.update.toDto())
            .map { it.toPatchResponse() }
            .onErrorResume { it.toPatchFailureResponse().toMono() }

    companion object {
        const val QUEUE_GROUP = "patch_order"
    }
}
