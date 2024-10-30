package com.makarytskyi.gateway.controller

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.core.fixtures.OrderRequestFixture.randomCreateRequest
import com.makarytskyi.core.fixtures.OrderRequestFixture.randomUpdateRequest
import com.makarytskyi.gateway.config.NatsClient
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.aggregatedOrderDto
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.createOrderResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.createRequest
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.deleteRequest
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failureCreateResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failureGetByIdResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failurePatchResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.findAllResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.listOfAggregatedOrderDto
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.patchRequest
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.randomAggregatedOrder
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulCreateResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulGetByIdResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulUpdateResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.updateOrderResponse
import com.makarytskyi.gateway.mapper.toProto
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersRequest
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.PatchOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.internalapi.subject.NatsSubject.Order.DELETE
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_ALL
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_BY_ID
import com.makarytskyi.internalapi.subject.NatsSubject.Order.PATCH
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

@ExtendWith(MockKExtension::class)
class OrderControllerTest {

    @MockK
    lateinit var natsClient: NatsClient

    @InjectMockKs
    lateinit var controller: OrderController

    @Test
    fun `getFullById should return order`() {
        val id = ObjectId().toString()
        val order = randomAggregatedOrder()
        val protoResponse = successfulGetByIdResponse(order)
        val aggregatedOrder = aggregatedOrderDto(protoResponse)

        every {
            natsClient.request(
                FIND_BY_ID,
                GetByIdOrderRequest.newBuilder().setId(id).build(),
                GetByIdOrderResponse.parser()
            )
        } returns protoResponse.toMono()

        controller.getFullById(id)
            .test()
            .expectNext(aggregatedOrder)
            .verifyComplete()
    }

    @Test
    fun `getFullById should return exception if natsClient returned exception`() {
        val id = ObjectId().toString()
        val exception = NotFoundException("Order with id is not found")
        val protoResponse = failureGetByIdResponse(exception)

        every {
            natsClient.request(
                FIND_BY_ID,
                GetByIdOrderRequest.newBuilder().setId(id).build(),
                GetByIdOrderResponse.parser()
            )
        } returns protoResponse.toMono()

        controller.getFullById(id)
            .test()
            .verifyError<NotFoundException>()
    }

    @Test
    fun `findFullAll should return orders`() {
        val page = 1
        val size = 10
        val protoResponse = findAllResponse()
        val aggregatedOrders = listOfAggregatedOrderDto(protoResponse)

        every {
            natsClient.request(
                FIND_ALL,
                FindAllOrdersRequest.newBuilder().setPage(page).setSize(size).build(),
                FindAllOrdersResponse.parser()
            )
        } returns protoResponse.toMono()

        controller.findFullAll(page, size).collectList()
            .test()
            .assertNext {
                assertThat(it).containsAll(aggregatedOrders)
            }
            .verifyComplete()
    }

    @Test
    fun `create should return created order`() {
        val price = 100.0
        val request = randomCreateRequest()
        val createResponse = successfulCreateResponse(request, price)
        val orderResponse = createOrderResponse(request, price).copy(id = createResponse.success.order.id)

        every {
            natsClient.request(
                CREATE,
                request.toProto(),
                CreateOrderResponse.parser()
            )
        } returns createResponse.toMono()

        controller.create(request)
            .test()
            .expectNext(orderResponse)
            .verifyComplete()
    }

    @Test
    fun `create should return exception if natsClient returned exception`() {
        val request = randomCreateRequest()
        val protoRequest = createRequest(request)
        val exception = IllegalArgumentException("Dates must be in future")
        val createResponse = failureCreateResponse(exception)

        every {
            natsClient.request(
                CREATE,
                protoRequest,
                CreateOrderResponse.parser()
            )
        } returns createResponse.toMono()

        controller.create(request)
            .test()
            .verifyError<IllegalArgumentException>()
    }

    @Test
    fun `delete should return Unit`() {
        val id = ObjectId().toString()

        every {
            natsClient.request(
                DELETE,
                deleteRequest(id),
                DeleteOrderResponse.parser()
            )
        } returns Mono.empty()

        controller.delete(id)
            .test()
            .expectNext(Unit)
            .verifyComplete()
    }

    @Test
    fun `patch should return updated order`() {
        val id = ObjectId().toString()
        val price = 100.0
        val updateRequest = randomUpdateRequest()
        val patchOrderRequest = patchRequest(id, updateRequest)
        val updateResponse = successfulUpdateResponse(updateRequest, price)
        val orderResponse = updateOrderResponse(updateRequest, price).copy(
            id = updateResponse.success.order.id,
            carId = updateResponse.success.order.carId,
            userId = updateResponse.success.order.userId,
        )

        every {
            natsClient.request(
                PATCH,
                patchOrderRequest,
                PatchOrderResponse.parser()
            )
        } returns updateResponse.toMono()

        controller.patch(id, updateRequest)
            .test()
            .expectNext(orderResponse)
            .verifyComplete()
    }

    @Test
    fun `patch should return exception if natsClient returned exception`() {
        val id = ObjectId().toString()
        val updateRequest = randomUpdateRequest()
        val patchOrderRequest = patchRequest(id, updateRequest)
        val exception = NotFoundException("Order with id is not found")
        val updateResponse = failurePatchResponse(exception)

        every {
            natsClient.request(
                PATCH,
                patchOrderRequest,
                PatchOrderResponse.parser()
            )
        } returns updateResponse.toMono()

        controller.patch(id, updateRequest)
            .test()
            .verifyError<NotFoundException>()
    }
}
