package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.subject.NatsSubject.Order.PATCH
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.controller.nats.order.CreateOrderNatsController.Companion
import com.makarytskyi.rentcar.mapper.toDto
import com.makarytskyi.rentcar.mapper.toProto
import com.makarytskyi.rentcar.proto.reqreply.patch.PatchOrderProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.patch.PatchOrderProtoResponse
import com.makarytskyi.rentcar.service.OrderService
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class PatchOrderNatsController(
    override val connection: Connection,
    private val orderService: OrderService
) : NatsController<PatchOrderProtoRequest, PatchOrderProtoResponse> {
    override val subject = PATCH
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<PatchOrderProtoRequest> = PatchOrderProtoRequest.parser()

    override fun handle(request: PatchOrderProtoRequest): Mono<PatchOrderProtoResponse> =
        orderService.patch(request.id, request.patch.toDto())
            .map { PatchOrderProtoResponse.newBuilder().apply { successBuilder.setOrder(it.toProto()) }.build() }
            .onErrorResume {
                PatchOrderProtoResponse.newBuilder()
                    .apply { errorBuilder.setMessage(it.message).setExceptionType(it.toProto()) }
                    .build().toMono()
            }

    companion object {
        const val QUEUE_GROUP = "patch_order"
    }
}
