package com.makarytskyi.gateway.controller

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.core.fixtures.OrderRequestFixture.randomCreateRequest
import com.makarytskyi.core.fixtures.OrderRequestFixture.randomUpdateRequest
import com.makarytskyi.gateway.config.NatsClient
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.aggregatedOrderDto
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.createOrderResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.createProtoRequest
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.deleteProtoRequest
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failureCreateProtoResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failureGetByIdProtoResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failureUpdateProtoResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.findAllProtoResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.listOfAggregatedOrderDto
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.patchProtoRequest
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulCreateProtoResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulGetByIdProtoResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulUpdateProtoResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.updateOrderResponse
import com.makarytskyi.gateway.mapper.toProto
import com.makarytskyi.internalapi.reqreply.create.CreateOrderProtoResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.internalapi.subject.NatsSubject.Order.DELETE
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_ALL
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_BY_ID
import com.makarytskyi.internalapi.subject.NatsSubject.Order.PATCH
import com.makarytskyi.rentcar.proto.reqreply.delete.DeleteOrderProtoResponse
import com.makarytskyi.rentcar.proto.reqreply.find_all.FindAllOrdersProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.find_all.FindAllOrdersProtoResponse
import com.makarytskyi.rentcar.proto.reqreply.find_by_id.GetByIdOrderProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.find_by_id.GetByIdOrderProtoResponse
import com.makarytskyi.rentcar.proto.reqreply.patch.PatchOrderProtoResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
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
        val protoResponse = successfulGetByIdProtoResponse()
        val aggregatedOrder = aggregatedOrderDto(protoResponse)

        every {
            natsClient.request(
                FIND_BY_ID,
                GetByIdOrderProtoRequest.newBuilder().setId(id).build(),
                GetByIdOrderProtoResponse.parser()
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
        val protoResponse = failureGetByIdProtoResponse()

        every {
            natsClient.request(
                FIND_BY_ID,
                GetByIdOrderProtoRequest.newBuilder().setId(id).build(),
                GetByIdOrderProtoResponse.parser()
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
        val protoResponse = findAllProtoResponse()
        val aggregatedOrders = listOfAggregatedOrderDto(protoResponse)

        every {
            natsClient.request(
                FIND_ALL,
                FindAllOrdersProtoRequest.newBuilder().setPage(page).setSize(size).build(),
                FindAllOrdersProtoResponse.parser()
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
        val createProtoResponse = successfulCreateProtoResponse(request, price)
        val orderResponse = createOrderResponse(request, price).copy(id = createProtoResponse.success.order.id)

        every {
            natsClient.request(
                CREATE,
                request.toProto(),
                CreateOrderProtoResponse.parser()
            )
        } returns createProtoResponse.toMono()

        controller.create(request)
            .test()
            .expectNext(orderResponse)
            .verifyComplete()
    }

    @Test
    fun `create should return exception if natsClient returned exception`() {
        val request = randomCreateRequest()
        val protoRequest = createProtoRequest(request)
        val createProtoResponse = failureCreateProtoResponse()

        every {
            natsClient.request(
                CREATE,
                protoRequest,
                CreateOrderProtoResponse.parser()
            )
        } returns createProtoResponse.toMono()

        controller.create(request)
            .test()
            .verifyError<IllegalArgumentException>()
    }

    @Test
    fun `delete should return nothing`() {
        val id = ObjectId().toString()

        every {
            natsClient.request(
                DELETE,
                deleteProtoRequest(id),
                DeleteOrderProtoResponse.parser()
            )
        } returns Mono.empty()

        controller.delete(id)
            .test()
            .verifyComplete()
    }

    @Test
    fun `patch should return updated order`() {
        val id = ObjectId().toString()
        val price = 100.0
        val updateRequest = randomUpdateRequest()
        val patchOrderRequest = patchProtoRequest(id, updateRequest)
        val updateProtoResponse = successfulUpdateProtoResponse(updateRequest, price)
        val orderResponse = updateOrderResponse(updateRequest, price).copy(
            id = updateProtoResponse.success.order.id,
            carId = updateProtoResponse.success.order.carId,
            userId = updateProtoResponse.success.order.userId,
        )

        every {
            natsClient.request(
                PATCH,
                patchOrderRequest,
                PatchOrderProtoResponse.parser()
            )
        } returns updateProtoResponse.toMono()

        controller.patch(id, updateRequest)
            .test()
            .expectNext(orderResponse)
            .verifyComplete()
    }

    @Test
    fun `patch should return exception if natsClient returned exception`() {
        val id = ObjectId().toString()
        val updateRequest = randomUpdateRequest()
        val patchOrderRequest = patchProtoRequest(id, updateRequest)
        val updateProtoResponse = failureUpdateProtoResponse()

        every {
            natsClient.request(
                PATCH,
                patchOrderRequest,
                PatchOrderProtoResponse.parser()
            )
        } returns updateProtoResponse.toMono()

        controller.patch(id, updateRequest)
            .test()
            .verifyError<NotFoundException>()
    }
}
