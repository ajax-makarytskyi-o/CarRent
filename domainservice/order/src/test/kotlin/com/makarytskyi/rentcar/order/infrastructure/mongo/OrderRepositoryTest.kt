package com.makarytskyi.rentcar.order.infrastructure.mongo

import com.makarytskyi.rentcar.car.application.port.output.CarRepositoryOutputPort
import com.makarytskyi.rentcar.fixtures.CarFixture.createCarRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.aggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderRequest
import com.makarytskyi.rentcar.fixtures.OrderFixture.createdOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.domainOrderPatch
import com.makarytskyi.rentcar.fixtures.OrderFixture.emptyOrderPatch
import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAndDayAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.UserFixture.createUserRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.order.ContainerBase
import com.makarytskyi.rentcar.order.application.port.output.OrderRepositoryOutputPort
import com.makarytskyi.rentcar.user.application.port.output.UserRepositoryOutputPort
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

internal class OrderRepositoryTest : ContainerBase {

    @Autowired
    lateinit var orderRepository: OrderRepositoryOutputPort

    @Autowired
    lateinit var carRepository: CarRepositoryOutputPort

    @Autowired
    lateinit var userRepository: UserRepositoryOutputPort

    @Test
    fun `create should insert order and return it with id`() {
        // GIVEN
        val car = carRepository.create(createCarRequest()).block()!!
        val user = userRepository.create(createUserRequest()).block()!!
        val order = createOrderRequest(car.id, user.id).copy(price = null)
        val expected = createdOrder(order)

        // WHEN
        val createdOrder = orderRepository.create(order)

        // THEN
        createdOrder
            .test()
            .assertNext {
                assertNotNull(it.id, "Order should have non-null id after saving")
                assertEquals(expected.copy(id = it.id), it)
            }
            .verifyComplete()
    }

    @Test
    fun `findAll should find all orders`() {
        // GIVEN
        val car1 = carRepository.create(createCarRequest()).block()!!
        val user1 = userRepository.create(createUserRequest()).block()!!
        val order1 = orderRepository.create(createOrderRequest(car1.id, user1.id)).block()!!
        val fullOrder1 = aggregatedOrder(order1, car1, user1)

        val car2 = carRepository.create(createCarRequest()).block()!!
        val user2 = userRepository.create(createUserRequest()).block()!!
        val order2 = orderRepository.create(createOrderRequest(car2.id, user2.id)).block()!!
        val fullOrder2 = aggregatedOrder(order2, car2, user2)

        // WHEN
        val allOrders = orderRepository.findFullAll(0, 20)

        // THEN
        allOrders.collectList()
            .test()
            .assertNext {
                assertThat(it).containsAll(listOf(fullOrder1, fullOrder2))
            }
            .verifyComplete()
    }

    @Test
    fun `patch should partially update order`() {
        // GIVEN
        val from = monthAfter
        val to = monthAndDayAfter
        val car = carRepository.create(createCarRequest()).block()!!
        val user = userRepository.create(createUserRequest()).block()!!
        val order = orderRepository.create(createOrderRequest(car.id, user.id)).block()!!

        val updateOrder = emptyOrderPatch().copy(
            from = from,
            to = to
        )

        val patch = domainOrderPatch(updateOrder, order)

        // WHEN
        val updated = orderRepository.patch(order.id, patch)

        // THEN
        updated
            .test()
            .assertNext {
                assertEquals(from, it.from)
                assertEquals(to, it.to)
            }
            .verifyComplete()
    }

    @Test
    fun `findById should return existing order by id`() {
        // GIVEN
        val car = carRepository.create(createCarRequest()).block()!!
        val user = userRepository.create(createUserRequest()).block()!!
        val order = orderRepository.create(createOrderRequest(car.id, user.id)).block()!!
        val fullOrder = aggregatedOrder(order, car, user)

        // WHEN
        val foundOrder = orderRepository.findFullById(order.id)

        // THEN
        foundOrder
            .test()
            .expectNext(fullOrder)
            .verifyComplete()
    }

    @Test
    fun `findById should return empty if cant find order by id`() {
        // GIVEN
        val unexistingId = ObjectId().toString()

        // WHEN
        val foundOrder = orderRepository.findFullById(unexistingId)

        // THEN
        foundOrder
            .test()
            .verifyComplete()
    }

    @Test
    fun `deleteById should delete order by id`() {
        // GIVEN
        val car = carRepository.create(createCarRequest()).block()!!
        val user = userRepository.create(createUserRequest()).block()!!
        val order = orderRepository.create(createOrderRequest(car.id, user.id)).block()!!

        // WHEN
        orderRepository.deleteById(order.id).block()!!

        // THEN
        orderRepository.findFullById(order.id)
            .test()
            .verifyComplete()
    }

    @Test
    fun `findByCarId should return orders found by carId`() {
        // GIVEN
        val car = carRepository.create(createCarRequest()).block()!!
        val user = userRepository.create(createUserRequest()).block()!!
        val order = orderRepository.create(createOrderRequest(car.id, user.id)).block()

        // WHEN
        val foundOrders = orderRepository.findByCarId(car.id)

        // THEN
        foundOrders.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(order)
            }
            .verifyComplete()
    }

    @Test
    fun `findByUserId should return orders found by userId`() {
        // GIVEN
        val car = carRepository.create(createCarRequest()).block()!!
        val user = userRepository.create(createUserRequest()).block()!!
        val order = orderRepository.create(createOrderRequest(car.id, user.id)).block()

        // WHEN
        val foundOrders = orderRepository.findByUserId(user.id)

        // THEN
        foundOrders.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(order)
            }
            .verifyComplete()
    }

    @Test
    fun `findByUserIdAndCarId should return orders found by userId and carId`() {
        // GIVEN
        val car = carRepository.create(createCarRequest()).block()!!
        val user = userRepository.create(createUserRequest()).block()!!
        val order = orderRepository.create(createOrderRequest(car.id, user.id)).block()

        // WHEN
        val foundOrders = orderRepository.findByCarIdAndUserId(car.id, user.id)

        // THEN
        foundOrders.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(order)
            }
            .verifyComplete()
    }
}
