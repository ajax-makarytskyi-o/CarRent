package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.subject.NatsSubject.Order.DELETE
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.controller.nats.order.CreateOrderNatsController.Companion
import com.makarytskyi.rentcar.proto.reqreply.delete.DeleteOrderProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.delete.DeleteOrderProtoResponse
import com.makarytskyi.rentcar.service.OrderService
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DeleteOrderNatsController(
    override val connection: Connection,
    private val orderService: OrderService
) : NatsController<DeleteOrderProtoRequest, DeleteOrderProtoResponse> {
    override val subject = DELETE
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<DeleteOrderProtoRequest> = DeleteOrderProtoRequest.parser()

    override fun handle(request: DeleteOrderProtoRequest): Mono<DeleteOrderProtoResponse> =
        orderService.deleteById(request.id)
            .map { DeleteOrderProtoResponse.newBuilder().apply { successBuilder }.build() }

    companion object {
        const val QUEUE_GROUP = "delete_order"
    }
}
