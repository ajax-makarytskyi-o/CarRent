package com.makarytskyi.rentcar.controller

import com.makarytskyi.core.dto.order.AggregatedOrderResponse
import com.makarytskyi.internalapi.model.order.AggregatedOrder
import com.makarytskyi.rentcar.controller.nats.order.FindAllOrdersNatsController
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.aggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseAggregatedOrder
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.findAllOrderProtoRequest
import com.makarytskyi.rentcar.mapper.toProto
import com.makarytskyi.rentcar.service.OrderService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.nats.client.Connection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
class FindAllOrdersNatsControllerTest {

    @MockK
    lateinit var orderService: OrderService

    @MockK
    lateinit var connection: Connection

    @InjectMockKs
    lateinit var findAllController: FindAllOrdersNatsController

    @Test
    fun `create should return success message with saved order`() {
        // GIVEN
        val page = 0
        val size = 10
        val car = randomCar()
        val user = randomUser()
        val order = aggregatedOrder(randomOrder(car.id, user.id), car, user)
        val protoRequest = findAllOrderProtoRequest(page, size)
        val orders: List<AggregatedOrderResponse> = listOf(responseAggregatedOrder(order, car))
        val protoOrders: List<AggregatedOrder> = orders.map { it.toProto() }

        //WHEN
        every { orderService.findAll(page, size) } returns orders.toFlux()

        //THEN
        findAllController.handle(protoRequest)
            .test()
            .assertNext {
                assertThat(it.success.ordersList).containsAll(protoOrders)
            }
            .verifyComplete()
    }

    @Test
    fun `create should return error message if service threw exception`() {
        // GIVEN
        val page = 0
        val size = 10
        val protoRequest = findAllOrderProtoRequest(page, size)

        //WHEN
        every { orderService.findAll(page, size) } returns Flux.empty()

        //THEN
        findAllController.handle(protoRequest)
            .test()
            .assertNext {
                assertThat(it.success.ordersList).hasSize(0)
            }
            .verifyComplete()
    }
}
