package com.makarytskyi.gateway.grpc

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.createOrderGrpcResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.createRequest
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failureCreateResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failureGetByIdResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.grpcGetByIdRequest
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.randomAggregatedOrder
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.randomPrice
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.streamCreatedOrderResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.streamCreatedOrdersRequest
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulCreateResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulGetByIdResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulGrpcGetByIdResponse
import com.makarytskyi.gateway.fixtures.request.OrderRequestFixture.grpcCreateRequest
import com.makarytskyi.gateway.fixtures.request.OrderRequestFixture.randomCreateRequest
import com.makarytskyi.gateway.fixtures.request.OrderRequestFixture.tomorrow
import com.makarytskyi.gateway.fixtures.request.OrderRequestFixture.yesterday
import com.makarytskyi.gateway.mapper.OrderMapper.toProto
import com.makarytskyi.grpcapi.input.reqreply.order.StreamCreatedOrdersByCar.StreamCreatedOrdersByUserIdResponse
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.internalapi.subject.NatsSubject.Order.GET_BY_ID
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError
import systems.ajax.nats.handler.api.NatsHandlerManager
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@ExtendWith(MockKExtension::class)
class OrderGrpcServiceTest {

    @MockK
    lateinit var manager: NatsHandlerManager

    @MockK
    lateinit var natsClient: NatsMessagePublisher

    @InjectMockKs
    lateinit var controller: OrderGrpcService

    @Test
    fun `getFullById should return order`() {
        // GIVEN
        val id = ObjectId().toString()
        val order = randomAggregatedOrder()
        val request = grpcGetByIdRequest(id)
        val protoResponse = successfulGetByIdResponse(order)
        val grpcProtoResponse = successfulGrpcGetByIdResponse(order)

        every {
            natsClient.request(
                GET_BY_ID,
                GetByIdOrderRequest.newBuilder().setId(id).build(),
                GetByIdOrderResponse.parser()
            )
        } returns protoResponse.toMono()

        // WHEN // THEN
        controller.getFullById(request)
            .test()
            .expectNext(grpcProtoResponse)
            .verifyComplete()
    }

    @Test
    fun `getFullById should return exception if NATS client returned exception`() {
        // GIVEN
        val id = ObjectId().toString()
        val request = grpcGetByIdRequest(id)
        val exception = NotFoundException("Order with id is not found")
        val protoResponse = failureGetByIdResponse(exception)

        every {
            natsClient.request(
                GET_BY_ID,
                GetByIdOrderRequest.newBuilder().setId(id).build(),
                GetByIdOrderResponse.parser()
            )
        } returns protoResponse.toMono()

        // WHEN // THEN
        controller.getFullById(request)
            .test()
            .verifyError<NotFoundException>()
    }

    @Test
    fun `create should return created order`() {
        // GIVEN
        val price = randomPrice()
        val request = randomCreateRequest()
        val grpcRequest = grpcCreateRequest(request)
        val createResponse = successfulCreateResponse(request, price)
        val grpcResponse = createOrderGrpcResponse(createResponse.success.order.id, request, price)

        every {
            natsClient.request(
                CREATE,
                request.toProto(),
                CreateOrderResponse.parser()
            )
        } returns createResponse.toMono()

        // WHEN // THEN
        controller.create(grpcRequest)
            .test()
            .expectNext(grpcResponse)
            .verifyComplete()
    }

    @Test
    fun `create should return exception if natsClient returned exception`() {
        // GIVEN
        val request = randomCreateRequest().copy(from = yesterday, to = tomorrow)
        val protoRequest = createRequest(request)
        val grpcProtoRequest = grpcCreateRequest(request)
        val exception = IllegalArgumentException("Dates must be in future")
        val createResponse = failureCreateResponse(exception)

        every {
            natsClient.request(
                CREATE,
                protoRequest,
                CreateOrderResponse.parser()
            )
        } returns createResponse.toMono()

        // WHEN // THEN
        controller.create(grpcProtoRequest)
            .test()
            .verifyError<IllegalArgumentException>()
    }

    @Test
    fun `streamCreatedOrdersByUserId should return list of created orders`() {
        // GIVEN
        val userId = ObjectId().toString()
        val grpcProtoRequest = streamCreatedOrdersRequest(userId)
        val orders = listOf(streamCreatedOrderResponse())

        every {
            manager.subscribe<StreamCreatedOrdersByUserIdResponse>(userId, any())
        } returns orders.toFlux()

        // WHEN // THEN
        controller.streamCreatedOrdersByUserId(grpcProtoRequest).collectList()
            .test()
            .assertNext {
                assertThat(it).containsAll(orders)
            }
    }
}
