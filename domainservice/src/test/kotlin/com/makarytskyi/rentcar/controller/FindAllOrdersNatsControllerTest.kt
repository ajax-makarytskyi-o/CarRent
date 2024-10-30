package com.makarytskyi.rentcar.controller

import com.makarytskyi.core.dto.order.AggregatedOrderResponseDto
import com.makarytskyi.internalapi.commonmodels.order.AggregatedOrder
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_ALL
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.aggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseAggregatedOrderDto
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.findAllOrderRequest
import com.makarytskyi.rentcar.mapper.toProto
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.ContainerBase
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import io.mockk.junit5.MockKExtension
import io.nats.client.Connection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
class FindAllOrdersNatsControllerTest : ContainerBase {

    @Autowired
    lateinit var connection: Connection

    @Autowired
    internal lateinit var carRepository: CarRepository

    @Autowired
    internal lateinit var userRepository: UserRepository

    @Autowired
    internal lateinit var orderRepository: OrderRepository

    @Test
    fun `findAll should return success message with list of orders`() {
        // GIVEN
        val page = 0
        val size = 10
        val car = carRepository.create(randomCar()).block()!!
        val user = userRepository.create(randomUser()).block()!!
        val order = orderRepository.create(randomOrder(car.id, user.id)).block()!!
        val aggregatedOrder = aggregatedOrder(order, car, user)

        val findAllRequest = findAllOrderRequest(page, size)
        val orders: List<AggregatedOrderResponseDto> = listOf(responseAggregatedOrderDto(aggregatedOrder, car))
        val protoOrders: List<AggregatedOrder> = orders.map { it.toProto() }

        //WHEN
        val response = connection.request(FIND_ALL, findAllRequest.toByteArray())

        //THEN
        val responseOrders = FindAllOrdersResponse.parser().parseFrom(response.get().data)
        assertThat(responseOrders.success.ordersList).containsAll(protoOrders)
        orderRepository.findFullAll(page, size).collectList()
            .test()
            .assertNext {
                assertThat(it).contains(aggregatedOrder)
            }
            .verifyComplete()
    }

    @Test
    fun `findAll should return message with empty list if service returned empty list`() {
        // GIVEN
        val page = 3
        val size = 10
        val findAllRequest = findAllOrderRequest(page, size)

        //WHEN
        val response = connection.request(FIND_ALL, findAllRequest.toByteArray())

        //THEN
        val responseOrders = FindAllOrdersResponse.parser().parseFrom(response.get().data)
        assertThat(responseOrders.success.ordersList).hasSize(0)
    }
}
