package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_BY_ID
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.mapper.toProto
import com.makarytskyi.rentcar.proto.reqreply.find_by_id.GetByIdOrderProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.find_by_id.GetByIdOrderProtoResponse
import com.makarytskyi.rentcar.service.OrderService
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class GetByIdOrderNatsController(
    override val connection: Connection,
    private val orderService: OrderService
) : NatsController<GetByIdOrderProtoRequest, GetByIdOrderProtoResponse> {
    override val subject = FIND_BY_ID
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<GetByIdOrderProtoRequest> = GetByIdOrderProtoRequest.parser()

    override fun handle(request: GetByIdOrderProtoRequest): Mono<GetByIdOrderProtoResponse> =
        orderService.getById(request.id)
            .map { GetByIdOrderProtoResponse.newBuilder().apply { successBuilder.setOrder(it.toProto()) }.build() }
            .onErrorResume {
                GetByIdOrderProtoResponse.newBuilder()
                    .apply { errorBuilder.setMessage(it.message).setExceptionType(it.toProto()) }
                    .build().toMono()
            }

    companion object {
        const val QUEUE_GROUP = "get_by_id_order"
    }
}
