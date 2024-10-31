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
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.defaultSize
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.emptySize
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.findAllOrderRequest
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.firstPage
import com.makarytskyi.rentcar.mapper.OrderMapper.toProto
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

class FindAllOrdersNatsControllerTest : AbstractOrderNatsControllerTest() {

    @Autowired
    internal lateinit var carRepository: CarRepository

    @Autowired
    internal lateinit var userRepository: UserRepository

    @Autowired
    internal lateinit var orderRepository: OrderRepository

    @Test
    fun `findAll should return success message with list of orders`() {
        // GIVEN
        val page = firstPage
        val size = defaultSize
        val car = carRepository.create(randomCar()).block()!!
        val user = userRepository.create(randomUser()).block()!!
        val order = orderRepository.create(randomOrder(car.id, user.id)).block()!!
        val aggregatedOrder = aggregatedOrder(order, car, user)

        val findAllRequest = findAllOrderRequest(page, size)
        val orders: List<AggregatedOrderResponseDto> = listOf(responseAggregatedOrderDto(aggregatedOrder, car))
        val protoOrders: List<AggregatedOrder> = orders.map { it.toProto() }

        // WHEN
        val response = sendRequest(FIND_ALL, findAllRequest, FindAllOrdersResponse.parser())

        // THEN
        assertThat(response.success.ordersList).containsAll(protoOrders)
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
        val page = firstPage
        val size = emptySize
        val findAllRequest = findAllOrderRequest(page, size)

        // WHEN
        val response = sendRequest(FIND_ALL, findAllRequest, FindAllOrdersResponse.parser())

        // THEN
        assertThat(response.success.ordersList).hasSize(0)
    }
}
