package com.makarytskyi.rentcar.repository

import fixtures.CarFixture.randomCar
import fixtures.OrderFixture.monthAfter
import fixtures.OrderFixture.monthAndDayAfter
import fixtures.OrderFixture.randomOrder
import fixtures.OrderFixture.unexistingOrder
import fixtures.UserFixture.randomUser
import java.util.Date
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class OrderRepositoryTests : ContainerBase {

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var carRepository: CarRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `create should insert order and return it with id`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val user = userRepository.create(randomUser())

        // WHEN
        val order = orderRepository.create(unexistingOrder(car.id, user.id))

        // THEN
        val foundOrder = orderRepository.findById(order.id.toString())
        assertNotNull(foundOrder)
        assertNotNull(order.id)
    }

    @Test
    fun `findAll should find all orders`() {
        // GIVEN
        val car1 = carRepository.create(randomCar())
        val user1 = userRepository.create(randomUser())
        val order1 = orderRepository.create(randomOrder(car1.id, user1.id))

        val car2 = carRepository.create(randomCar())
        val user2 = userRepository.create(randomUser())
        val order2 = orderRepository.create(randomOrder(car2.id, user2.id))

        // WHEN
        val orders = orderRepository.findAll()

        // THEN
        assertTrue(orders.any { it.id == order1.id && it.car?.id == order1.carId && it.user?.id == order1.userId })
        assertTrue(orders.any { it.id == order2.id && it.car?.id == order2.carId && it.user?.id == order2.userId })
    }

    @Test
    fun `update should update start date`() {
        // GIVEN
        val from = Date.from(monthAfter.toInstant())
        val car = carRepository.create(randomCar())
        val user = userRepository.create(randomUser())
        val order = orderRepository.create(randomOrder(car.id, user.id))

        val update = order.copy(
            from = from,
            to = null
        )

        // WHEN
        orderRepository.update(order.id.toString(), update)

        // THEN
        val updated = orderRepository.findById(order.id.toString())
        assertEquals(from, updated?.from)
        assertEquals(order.to, updated?.to)
    }

    @Test
    fun `update should update end date`() {
        // GIVEN
        val to = Date.from(monthAndDayAfter.toInstant())
        val car = carRepository.create(randomCar())
        val user = userRepository.create(randomUser())
        val order = orderRepository.create(randomOrder(car.id, user.id))

        val update = order.copy(
            from = null,
            to = to
        )

        // WHEN
        orderRepository.update(order.id.toString(), update)

        // THEN
        val updated = orderRepository.findById(order.id.toString())
        assertEquals(to, updated?.to)
        assertEquals(order.from, updated?.from)
    }

    @Test
    fun `findById should return existing order by id`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val user = userRepository.create(randomUser())
        val order = orderRepository.create(randomOrder(car.id, user.id))

        // WHEN
        val foundOrder = orderRepository.findById(order.id.toString())

        // THEN
        assertNotNull(foundOrder)
    }

    @Test
    fun `findById should return null if cant find order by id`() {
        // GIVEN
        val unexistingId = "unexistingId"

        // WHEN
        val foundOrder = orderRepository.findById(unexistingId)

        // THEN
        assertNull(foundOrder)
    }

    @Test
    fun `deleteById should delete order by id`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val user = userRepository.create(randomUser())
        val order = orderRepository.create(randomOrder(car.id, user.id))

        val createdOrder = orderRepository.findById(order.id.toString())

        // WHEN
        orderRepository.deleteById(order.id.toString())

        // THEN
        val deletedRepairing = orderRepository.findById(order.id.toString())
        assertNotNull(createdOrder)
        assertNull(deletedRepairing)
    }

    @Test
    fun `findByCarId should return orders found by carId`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val user = userRepository.create(randomUser())
        orderRepository.create(randomOrder(car.id, user.id))

        // WHEN
        val foundOrders = orderRepository.findByCarId(car.id.toString())

        // THEN
        assertTrue(foundOrders.isNotEmpty())
    }

    @Test
    fun `findByUserId should return orders found by userId`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val user = userRepository.create(randomUser())
        orderRepository.create(randomOrder(car.id, user.id))

        // WHEN
        val foundOrders = orderRepository.findByUserId(user.id.toString())

        // THEN
        assertTrue(foundOrders.isNotEmpty())
    }

    @Test
    fun `findByUserIdAndCarId should return orders found by userId and carId`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val user = userRepository.create(randomUser())
        orderRepository.create(randomOrder(car.id, user.id))

        // WHEN
        val foundOrders = orderRepository.findByCarIdAndUserId(car.id.toString(), user.id.toString())

        // THEN
        assertTrue(foundOrders.isNotEmpty())
    }
}
