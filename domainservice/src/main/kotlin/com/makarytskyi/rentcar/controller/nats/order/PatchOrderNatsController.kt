package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.PatchOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.PatchOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.PATCH
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.mapper.OrderMapper.toDto
import com.makarytskyi.rentcar.mapper.OrderMapper.toPatchResponse
import com.makarytskyi.rentcar.mapper.toPatchFailureResponse
import com.makarytskyi.rentcar.service.OrderService
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class PatchOrderNatsController(
    override val connection: Connection,
    private val orderService: OrderService
) : NatsController<PatchOrderRequest, PatchOrderResponse> {
    override val subject = PATCH
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<PatchOrderRequest> = PatchOrderRequest.parser()
    override val defaultResponse: PatchOrderResponse = PatchOrderResponse.newBuilder()
        .also { it.failureBuilder.message = "Error happend during parsing." }.build()

    override fun handle(request: PatchOrderRequest): Mono<PatchOrderResponse> =
        orderService.patch(request.id, request.patch.toDto())
            .map { it.toPatchResponse() }
            .onErrorResume { it.toPatchFailureResponse().toMono() }

    companion object {
        const val QUEUE_GROUP = "patch_order"
    }
}
