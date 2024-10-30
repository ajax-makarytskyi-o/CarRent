package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.DELETE
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.mapper.toDeleteResponse
import com.makarytskyi.rentcar.service.OrderService
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DeleteOrderNatsController(
    override val connection: Connection,
    private val orderService: OrderService
) : NatsController<DeleteOrderRequest, DeleteOrderResponse> {
    override val subject = DELETE
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<DeleteOrderRequest> = DeleteOrderRequest.parser()
    override val defaultResponse: DeleteOrderResponse = DeleteOrderResponse.getDefaultInstance()

    override fun handle(request: DeleteOrderRequest): Mono<DeleteOrderResponse> =
        orderService.deleteById(request.id)
            .thenReturn(toDeleteResponse())

    companion object {
        const val QUEUE_GROUP = "delete_order"
    }
}
