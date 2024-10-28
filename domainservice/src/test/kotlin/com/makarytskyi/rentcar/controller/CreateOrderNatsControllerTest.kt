package com.makarytskyi.rentcar.controller

import com.makarytskyi.rentcar.controller.nats.order.CreateOrderNatsController
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderEntity
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderRequest
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseOrder
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.createOrderProtoRequest
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.failCreateProtoResponse
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.successCreateProtoResponse
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
class CreateOrderNatsControllerTest {

    @MockK
    lateinit var orderService: OrderService

    @MockK
    lateinit var connection: Connection

    @InjectMockKs
    lateinit var createController: CreateOrderNatsController

    @Test
    fun `create should return success message with saved order`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val request = createOrderRequest(car, user)
        val protoRequest = createOrderProtoRequest(request)
        val response = responseOrder(createOrderEntity(request).copy(id = ObjectId()), car)
        val protoResponse = successCreateProtoResponse(response)

        //WHEN
        every { orderService.create(request) } returns response.toMono()

        //THEN
        createController.handle(protoRequest)
            .test()
            .expectNext(protoResponse)
            .verifyComplete()
    }

    @Test
    fun `create should return error message if service threw exception`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val request = createOrderRequest(car, user)
        val protoRequest = createOrderProtoRequest(request)
        val exception = IllegalArgumentException("Car was not found")
        val protoResponse = failCreateProtoResponse(exception)

        //WHEN
        every { orderService.create(request) } returns exception.toMono()

        //THEN
        createController.handle(protoRequest)
            .test()
            .expectNext(protoResponse)
            .verifyComplete()
    }
}
