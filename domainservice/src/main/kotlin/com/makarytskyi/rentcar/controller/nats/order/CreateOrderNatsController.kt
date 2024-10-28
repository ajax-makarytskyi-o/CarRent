package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.reqreply.create.CreateOrderProtoRequest
import com.makarytskyi.internalapi.reqreply.create.CreateOrderProtoResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.mapper.toDto
import com.makarytskyi.rentcar.mapper.toProto
import com.makarytskyi.rentcar.service.OrderService
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CreateOrderNatsController(
    override val connection: Connection,
    private val orderService: OrderService
) : NatsController<CreateOrderProtoRequest, CreateOrderProtoResponse> {
    override val subject = CREATE
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<CreateOrderProtoRequest> = CreateOrderProtoRequest.parser()

    override fun handle(request: CreateOrderProtoRequest): Mono<CreateOrderProtoResponse> =
        orderService.create(request.toDto())
            .map { CreateOrderProtoResponse.newBuilder().apply { successBuilder.setOrder(it.toProto()) }.build() }
            .onErrorResume {
                CreateOrderProtoResponse.newBuilder()
                    .apply { errorBuilder.setMessage(it.message).setExceptionType(it.toProto()) }
                    .build().toMono()
            }

    companion object {
        const val QUEUE_GROUP = "create_order"
    }
}
