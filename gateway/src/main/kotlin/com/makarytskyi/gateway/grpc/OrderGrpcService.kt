package com.makarytskyi.gateway.grpc

import com.makarytskyi.gateway.config.NatsClient
import com.makarytskyi.gateway.mapper.OrderMapper.toGrpcProto
import com.makarytskyi.gateway.mapper.OrderMapper.toInternalProto
import com.makarytskyi.grpcapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.grpcapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.grpcapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.grpcapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.grpcapi.input.reqreply.order.StreamCreatedOrdersByCar.StreamCreatedOrdersByUserIdRequest
import com.makarytskyi.grpcapi.input.reqreply.order.StreamCreatedOrdersByCar.StreamCreatedOrdersByUserIdResponse
import com.makarytskyi.grpcapi.service.ReactorOrderServiceGrpc
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.internalapi.subject.NatsSubject.Order.GET_BY_ID
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse as InternalCreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse as InternalGetByIdOrderResponse

@GrpcService
class OrderGrpcService(
    private val natsClient: NatsClient
) : ReactorOrderServiceGrpc.OrderServiceImplBase() {

    override fun getFullById(request: GetByIdOrderRequest): Mono<GetByIdOrderResponse> =
        natsClient.request(
            GET_BY_ID,
            request.toInternalProto(),
            InternalGetByIdOrderResponse.parser()
        )
            .map { it.toGrpcProto() }

    override fun create(request: CreateOrderRequest): Mono<CreateOrderResponse> =
        natsClient.request(
            CREATE,
            request.toInternalProto(),
            InternalCreateOrderResponse.parser()
        )
            .map { it.toGrpcProto() }

    override fun streamCreatedOrdersByUserId(request: StreamCreatedOrdersByUserIdRequest):
            Flux<StreamCreatedOrdersByUserIdResponse> =
        natsClient.streamCreatedOrdersByCarId(request.userId)
}
