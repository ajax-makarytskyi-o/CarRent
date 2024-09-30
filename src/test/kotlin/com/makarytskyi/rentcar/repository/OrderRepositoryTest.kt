package com.makarytskyi.rentcar.repository

import fixtures.CarFixture.randomCar
import fixtures.OrderFixture.monthAfter
import fixtures.OrderFixture.monthAndDayAfter
import fixtures.OrderFixture.randomOrder
import fixtures.UserFixture.randomUser
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

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
        val car = carRepository.create(randomCar())
        val user = userRepository.create(randomUser())

        // WHEN
        val order = orderRepository.create(randomOrder(car.id, user.id).copy(id = null))

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
        assertTrue(orders.any { it.id == order1.id })
        assertTrue(orders.any { it.id == order2.id })
    }

    @Test
    fun `update should update order`() {
        // GIVEN
        val from = monthAfter
        val to = monthAndDayAfter
        val car = carRepository.create(randomCar())
        val user = userRepository.create(randomUser())
        val order = orderRepository.create(randomOrder(car.id, user.id))

        val updateOrder = order.copy(
            from = from,
            to = to
        )

        // WHEN
        val updated = orderRepository.update(order.id.toString(), updateOrder)

        // THEN
        assertEquals(from, updated?.from)
        assertEquals(to, updated?.to)
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
        assertEquals(order.id, foundOrder?.id)
        assertEquals(order.carId, foundOrder?.car?.id)
        assertEquals(order.userId, foundOrder?.user?.id)
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

        // WHEN
        orderRepository.deleteById(order.id.toString())

        // THEN
        val deletedRepairing = orderRepository.findById(order.id.toString())
        assertNull(deletedRepairing)
    }

    @Test
    fun `findByCarId should return orders found by carId`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val user = userRepository.create(randomUser())
        val order = orderRepository.create(randomOrder(car.id, user.id))

        // WHEN
        val foundOrders = orderRepository.findByCarId(car.id.toString())

        // THEN
        assertTrue(foundOrders.contains(order))
    }

    @Test
    fun `findByUserId should return orders found by userId`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val user = userRepository.create(randomUser())
        val order = orderRepository.create(randomOrder(car.id, user.id))

        // WHEN
        val foundOrders = orderRepository.findByUserId(user.id.toString())

        // THEN
        assertTrue(foundOrders.contains(order))
    }

    @Test
    fun `findByUserIdAndCarId should return orders found by userId and carId`() {
        // GIVEN
        val car = carRepository.create(randomCar())
        val user = userRepository.create(randomUser())
        val order = orderRepository.create(randomOrder(car.id, user.id))

        // WHEN
        val foundOrders = orderRepository.findByCarIdAndUserId(car.id.toString(), user.id.toString())

        // THEN
        assertTrue(foundOrders.contains(order))
    }
}
