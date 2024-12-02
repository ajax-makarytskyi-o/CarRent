package com.makarytskyi.rentcar.order.infrastructure.nats

import com.makarytskyi.commonmodels.order.AggregatedOrder
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_ALL
import com.makarytskyi.rentcar.car.application.port.output.CarOutputPort
import com.makarytskyi.rentcar.order.application.port.output.OrderMongoOutputPort
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.aggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseAggregatedOrderDto
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.Utils.defaultSize
import com.makarytskyi.rentcar.fixtures.Utils.emptySize
import com.makarytskyi.rentcar.fixtures.Utils.firstPage
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.findAllOrderRequest
import com.makarytskyi.rentcar.order.ContainerBase
import com.makarytskyi.rentcar.order.domain.projection.AggregatedDomainOrder
import com.makarytskyi.rentcar.order.infrastructure.nats.mapper.OrderProtoMapper.toProto
import com.makarytskyi.rentcar.order.infrastructure.nats.mapper.toProto
import com.makarytskyi.rentcar.user.application.port.output.UserOutputPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class FindAllOrdersNatsControllerTest : ContainerBase {

    @Autowired
    internal lateinit var natsPublisher: NatsMessagePublisher

    @Autowired
    internal lateinit var carRepository: CarOutputPort

    @Autowired
    internal lateinit var userRepository: UserOutputPort

    @Autowired
    internal lateinit var orderRepository: OrderMongoOutputPort

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
        val orders: List<AggregatedDomainOrder> = listOf(responseAggregatedOrderDto(aggregatedOrder, car))
        val protoOrders: List<AggregatedOrder> = orders.map { it.toProto() }

        // WHEN
        val response = natsPublisher.request(FIND_ALL, findAllRequest, FindAllOrdersResponse.parser()).block()!!

        // THEN
        assertThat(response.success.ordersList).containsAll(protoOrders)
        val actualOrders = orderRepository.findFullAll(page, size).collectList().block()!!
        assertThat(actualOrders).contains(aggregatedOrder)
    }

    @Test
    fun `findAll should return message with empty list if service returned empty list`() {
        // GIVEN
        val page = firstPage
        val size = emptySize
        val findAllRequest = findAllOrderRequest(page, size)

        // WHEN
        val response = natsPublisher.request(FIND_ALL, findAllRequest, FindAllOrdersResponse.parser()).block()!!

        // THEN
        assertThat(response.success.ordersList).hasSize(0)
    }
}
