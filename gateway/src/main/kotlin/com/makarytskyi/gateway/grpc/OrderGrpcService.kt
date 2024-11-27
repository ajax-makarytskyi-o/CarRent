package com.makarytskyi.gateway.grpc

import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.gateway.mapper.OrderMapper.toGrpcProto
import com.makarytskyi.gateway.mapper.OrderMapper.toInternalProto
import com.makarytskyi.grpcapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.grpcapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.grpcapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.grpcapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.grpcapi.input.reqreply.order.StreamCreatedOrdersByCar.StreamCreatedOrdersByUserIdRequest
import com.makarytskyi.grpcapi.input.reqreply.order.StreamCreatedOrdersByCar.StreamCreatedOrdersByUserIdResponse
import com.makarytskyi.grpcapi.service.ReactorOrderServiceGrpc
import com.makarytskyi.internalapi.subject.NatsSubject
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.internalapi.subject.NatsSubject.Order.GET_BY_ID
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.nats.handler.api.NatsHandlerManager
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse as InternalCreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse as InternalGetByIdOrderResponse

@GrpcService
class OrderGrpcService(
    private val natsPublisher: NatsMessagePublisher,
    private val manager: NatsHandlerManager,
) : ReactorOrderServiceGrpc.OrderServiceImplBase() {

    override fun getFullById(request: GetByIdOrderRequest): Mono<GetByIdOrderResponse> =
        natsPublisher.request(
            GET_BY_ID,
            request.toInternalProto(),
            InternalGetByIdOrderResponse.parser()
        )
            .map { it.toGrpcProto() }

    override fun create(request: CreateOrderRequest): Mono<CreateOrderResponse> =
        natsPublisher.request(
            CREATE,
            request.toInternalProto(),
            InternalCreateOrderResponse.parser()
        )
            .map { it.toGrpcProto() }

    override fun streamCreatedOrdersByUserId(request: Mono<StreamCreatedOrdersByUserIdRequest>):
            Flux<StreamCreatedOrdersByUserIdResponse> =
        request.flatMapMany {
            manager.subscribe(NatsSubject.Order.createOrderOnCar(it.userId)) {
                val order = Order.parser().parseFrom(it.data)
                StreamCreatedOrdersByUserIdResponse.newBuilder().also { response -> response.order = order }.build()
            }
        }
}
