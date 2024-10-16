package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.emptyOrderPatch
import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAndDayAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

internal class OrderRepositoryTest : ContainerBase {

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var carRepository: CarRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `create should insert order and return it with id`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()
        val user = userRepository.create(randomUser()).block()
        val order = randomOrder(car?.id, user?.id).copy(id = null)

        // WHEN
        val createdOrder = orderRepository.create(order)

        // THEN
        StepVerifier.create(createdOrder)
            .assertNext {
                assertNotNull(it.id)
                assertEquals(order.copy(id = it.id), it)
            }
            .verifyComplete()
    }

    @Test
    fun `findAll should find all orders`() {
        // GIVEN
        val car1 = carRepository.create(randomCar()).block()
        val user1 = userRepository.create(randomUser()).block()
        val order1 = orderRepository.create(randomOrder(car1?.id, user1?.id)).block()

        val car2 = carRepository.create(randomCar()).block()
        val user2 = userRepository.create(randomUser()).block()
        val order2 = orderRepository.create(randomOrder(car2?.id, user2?.id)).block()

        // WHEN
        val allOrders = orderRepository.findFullAll(0, 20)

        // THEN
        StepVerifier.create(allOrders.collectList())
            .assertNext { orders ->
                assertTrue(orders.any { it.id == order1?.id })
                assertTrue(orders.any { it.id == order2?.id })
            }
            .verifyComplete()
    }

    @Test
    fun `patch should partially update order`() {
        // GIVEN
        val from = monthAfter
        val to = monthAndDayAfter
        val car = carRepository.create(randomCar()).block()
        val user = userRepository.create(randomUser()).block()
        val order = orderRepository.create(randomOrder(car?.id, user?.id)).block()

        val updateOrder = emptyOrderPatch().copy(
            from = from,
            to = to
        )

        // WHEN
        val updated = orderRepository.patch(order?.id.toString(), updateOrder)

        // THEN
        StepVerifier.create(updated)
            .assertNext {
                assertEquals(from, it.from)
                assertEquals(to, it.to)
            }
            .verifyComplete()
    }

    @Test
    fun `findById should return existing order by id`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()
        val user = userRepository.create(randomUser()).block()
        val order = orderRepository.create(randomOrder(car?.id, user?.id)).block()

        // WHEN
        val foundOrder = orderRepository.findFullById(order?.id.toString())

        // THEN
        StepVerifier.create(foundOrder)
            .assertNext {
                assertEquals(order?.id, it.id)
                assertEquals(order?.carId, it.car?.id)
                assertEquals(order?.userId, it.user?.id)
            }
            .verifyComplete()
    }

    @Test
    fun `findById should not return anything if cant find order by id`() {
        // GIVEN
        val unexistingId = "unexistingId"

        // WHEN
        val foundOrder = orderRepository.findFullById(unexistingId)

        // THEN
        StepVerifier.create(foundOrder)
            .verifyComplete()
    }

    @Test
    fun `deleteById should delete order by id`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()
        val user = userRepository.create(randomUser()).block()
        val order = orderRepository.create(randomOrder(car?.id, user?.id)).block()

        // WHEN
        orderRepository.deleteById(order?.id.toString()).block()

        // THEN
        StepVerifier.create(orderRepository.findFullById(order?.id.toString()))
            .verifyComplete()
    }

    @Test
    fun `findByCarId should return orders found by carId`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()
        val user = userRepository.create(randomUser()).block()
        val order = orderRepository.create(randomOrder(car?.id, user?.id)).block()

        // WHEN
        val foundOrders = orderRepository.findByCarId(car?.id.toString())

        // THEN
        StepVerifier.create(foundOrders.collectList())
            .assertNext {
                assertTrue(it.contains(order))
            }
            .verifyComplete()
    }

    @Test
    fun `findByUserId should return orders found by userId`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()
        val user = userRepository.create(randomUser()).block()
        val order = orderRepository.create(randomOrder(car?.id, user?.id)).block()

        // WHEN
        val foundOrders = orderRepository.findByUserId(user?.id.toString())

        // THEN
        StepVerifier.create(foundOrders.collectList())
            .assertNext {
                assertTrue(it.contains(order))
            }
            .verifyComplete()
    }

    @Test
    fun `findByUserIdAndCarId should return orders found by userId and carId`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()
        val user = userRepository.create(randomUser()).block()
        val order = orderRepository.create(randomOrder(car?.id, user?.id)).block()

        // WHEN
        val foundOrders = orderRepository.findByCarIdAndUserId(car?.id.toString(), user?.id.toString())

        // THEN
        StepVerifier.create(foundOrders.collectList())
            .assertNext {
                assertTrue(it.contains(order))
            }
            .verifyComplete()
    }
}
