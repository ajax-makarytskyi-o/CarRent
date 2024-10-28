package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_ALL
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.controller.nats.order.CreateOrderNatsController.Companion
import com.makarytskyi.rentcar.mapper.toProto
import com.makarytskyi.rentcar.proto.reqreply.find_all.FindAllOrdersProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.find_all.FindAllOrdersProtoResponse
import com.makarytskyi.rentcar.service.OrderService
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class FindAllOrdersNatsController(
    override val connection: Connection,
    private val orderService: OrderService
) : NatsController<FindAllOrdersProtoRequest, FindAllOrdersProtoResponse> {
    override val subject = FIND_ALL
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<FindAllOrdersProtoRequest> = FindAllOrdersProtoRequest.parser()

    override fun handle(request: FindAllOrdersProtoRequest): Mono<FindAllOrdersProtoResponse> =
        orderService.findAll(request.page, request.size)
            .map { it.toProto() }
            .collectList()
            .map { FindAllOrdersProtoResponse.newBuilder().apply { successBuilder.addAllOrders(it) }.build() }
            .onErrorResume {
                FindAllOrdersProtoResponse.newBuilder()
                    .apply { errorBuilder.setMessage(it.message).setExceptionType(it.toProto()) }
                    .build().toMono()
            }

    companion object {
        const val QUEUE_GROUP = "find_all_orders"
    }
}
