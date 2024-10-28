package com.makarytskyi.rentcar.controller

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.rentcar.controller.nats.order.PatchOrderNatsController
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.updateOrderRequest
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.failPatchProtoResponse
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.successPatchProtoResponse
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.updateOrderProtoRequest
import com.makarytskyi.rentcar.service.OrderService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.nats.client.Connection
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
class PatchOrderNatsControllerTest {

    @MockK
    lateinit var orderService: OrderService

    @MockK
    lateinit var connection: Connection

    @InjectMockKs
    lateinit var patchController: PatchOrderNatsController

    @Test
    fun `create should return success message with saved order`() {
        // GIVEN
        val id = ObjectId().toString()
        val car = randomCar()
        val request = updateOrderRequest()
        val protoRequest = updateOrderProtoRequest(id, request)
        val updatedOrder = randomOrder(ObjectId(), ObjectId()).copy(from = request.from, to = request.to)
        val response = responseOrder(updatedOrder, car).copy(id = id)
        val protoResponse = successPatchProtoResponse(response)

        //WHEN
        every { orderService.patch(id, request) } returns response.toMono()

        //THEN
        patchController.handle(protoRequest)
            .test()
            .expectNext(protoResponse)
            .verifyComplete()
    }

    @Test
    fun `create should return error message if service threw exception`() {
        // GIVEN
        val id = ObjectId().toString()
        val request = updateOrderRequest()
        val exception = NotFoundException("Order was not found")
        val protoRequest = updateOrderProtoRequest(id, request)
        val protoResponse = failPatchProtoResponse(exception)

        //WHEN
        every { orderService.patch(id, request) } returns exception.toMono()

        //THEN
        patchController.handle(protoRequest)
            .test()
            .expectNext(protoResponse)
            .verifyComplete()
    }
}
