package com.makarytskyi.rentcar.controller

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.rentcar.controller.nats.order.GetByIdOrderNatsController
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomAggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseAggregatedOrder
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.failGetByIdProtoResponse
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.getByIdOrderProtoRequest
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.successGetByIdProtoResponse
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
class GetByIdOrderNatsControllerTest {

    @MockK
    lateinit var orderService: OrderService

    @MockK
    lateinit var connection: Connection

    @InjectMockKs
    lateinit var getController: GetByIdOrderNatsController

    @Test
    fun `create should return success message with saved order`() {
        // GIVEN
        val id = ObjectId().toString()
        val car = randomCar()
        val user = randomUser()
        val protoRequest = getByIdOrderProtoRequest(id)
        val order = randomAggregatedOrder(car, user).copy(id = ObjectId())
        val response = responseAggregatedOrder(order, car)
        val protoResponse = successGetByIdProtoResponse(response)

        //WHEN
        every { orderService.getById(id) } returns response.toMono()

        //THEN
        getController.handle(protoRequest)
            .test()
            .expectNext(protoResponse)
            .verifyComplete()
    }

    @Test
    fun `create should return error message if service threw exception`() {
        // GIVEN
        val id = ObjectId().toString()
        val protoRequest = getByIdOrderProtoRequest(id)
        val exception = NotFoundException("Order with id $id is not found")
        val protoResponse = failGetByIdProtoResponse(exception)

        //WHEN
        every { orderService.getById(id) } returns exception.toMono()

        //THEN
        getController.handle(protoRequest)
            .test()
            .expectNext(protoResponse)
            .verifyComplete()
    }
}
