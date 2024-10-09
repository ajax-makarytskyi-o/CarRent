package com.makarytskyi.rentcar.services

import com.makarytskyi.rentcar.exception.NotFoundException
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderEntity
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderRequest
import com.makarytskyi.rentcar.fixtures.OrderFixture.createdOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAndDayAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.orderPatch
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomAggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseAggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.tomorrow
import com.makarytskyi.rentcar.fixtures.OrderFixture.updateOrderRequest
import com.makarytskyi.rentcar.fixtures.OrderFixture.updatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.yesterday
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.service.impl.OrderServiceImpl
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class OrderServiceTest {
    @Mock
    lateinit var orderRepository: OrderRepository

    @Mock
    lateinit var carRepository: CarRepository

    @Mock
    lateinit var userRepository: UserRepository

    @InjectMocks
    lateinit var orderService: OrderServiceImpl

    @Test
    fun `getById should return OrderResponse when Order exists`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val order = randomAggregatedOrder(car, user)
        val response = responseAggregatedOrder(order, car)
        whenever(orderRepository.findById(order.id.toString())).thenReturn(order)

        // WHEN
        val result = orderService.getById(order.id.toString())

        // THEN
        assertEquals(response, result)
        verify(orderRepository).findById(order.id.toString())
    }

    @Test
    fun `getById should return throw ResourceNotFoundException`() {
        // GIVEN
        val orderId = ObjectId()
        whenever(orderRepository.findById(orderId.toString())).thenReturn(null)

        // WHEN // THEN
        assertThrows(NotFoundException::class.java, { orderService.getById(orderId.toString()) })
        verify(orderRepository).findById(orderId.toString())
    }

    @Test
    fun `findAll should return List of OrderResponse`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val order = randomAggregatedOrder(car, user)
        val response = responseAggregatedOrder(order, car)
        val orders = listOf(order)
        val expected = listOf(response)
        whenever(orderRepository.findAll(0, 10)).thenReturn(orders)

        // WHEN
        val result = orderService.findAll(0, 10)

        // THEN
        assertEquals(expected, result)
        verify(orderRepository).findAll(0, 10)
    }

    @Test
    fun `findAll should return empty List of OrderResponse if repository return empty List`() {
        // GIVEN
        whenever(orderRepository.findAll(0, 10)).thenReturn(emptyList())

        // WHEN
        val result = orderService.findAll(0, 10)

        // THEN
        assertEquals(emptyList(), result)
        verify(orderRepository).findAll(0, 10)
    }

    @Test
    fun `should create order successfully`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val request = createOrderRequest(car, user)
        val requestEntity = createOrderEntity(request)
        val createdOrder = createdOrder(requestEntity)
        val response = responseOrder(createdOrder, car)
        whenever(orderRepository.create(requestEntity)).thenReturn(createdOrder)
        whenever(carRepository.findById(car.id.toString())).thenReturn(car)
        whenever(userRepository.findById(user.id.toString())).thenReturn(user)

        // WHEN
        val result = orderService.create(request)

        // THEN
        assertEquals(response, result)
        verify(orderRepository).create(requestEntity)
    }

    @Test
    fun `should throw IllegalArgumentException if car doesn't exist`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        whenever(userRepository.findById(user.id.toString())).thenReturn(user)
        whenever(carRepository.findById(car.id.toString())).thenReturn(null)

        // WHEN // THEN
        assertThrows(NotFoundException::class.java, { orderService.create(createOrderRequest(car, user)) })
    }

    @Test
    fun `should throw IllegalArgumentException if user doesn't exist`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val request = createOrderRequest(car, user)
        whenever(userRepository.findById(user.id.toString())).thenReturn(null)

        // WHEN // THEN
        assertThrows(IllegalArgumentException::class.java, { orderService.create(request) })
    }

    @Test
    fun `should throw IllegalArgumentException if car is ordered`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        whenever(userRepository.findById(user.id.toString())).thenReturn(user)
        whenever(carRepository.findById(car.id.toString())).thenReturn(car)
        whenever(orderRepository.findByCarId(car.id.toString())).thenReturn(
            listOf(
                randomOrder(car.id, user.id).copy(
                    from = monthAfter,
                    to = monthAndDayAfter
                )
            )
        )

        // WHEN // THEN
        assertThrows(IllegalArgumentException::class.java, { orderService.create(createOrderRequest(car, user)) })
        verify(orderRepository).findByCarId(car.id.toString())
    }

    @Test
    fun `should throw IllegalArgumentException if dates reversed`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val request = createOrderRequest(car, user).copy(
            from = monthAfter,
            to = tomorrow
        )

        // WHEN // THEN
        assertThrows(IllegalArgumentException::class.java, { orderService.create(request) })
    }

    @Test
    fun `should throw IllegalArgumentException if date in past`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val request = createOrderRequest(car, user).copy(
            from = yesterday,
            to = tomorrow
        )

        // WHEN // THEN
        assertThrows(IllegalArgumentException::class.java, { orderService.create(request) })
    }

    @Test
    fun `patch should return updated order with start date`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val order = randomAggregatedOrder(car, user)
        val price = car.price?.multiply(BigDecimal(1))
        val request = updateOrderRequest().copy(from = tomorrow, to = null)
        val requestEntity = orderPatch(request)
        val updatedOrder = updatedOrder(order, request)
        val response = responseOrder(updatedOrder, car).copy(price = price)
        whenever(orderRepository.findById(order.id.toString())).thenReturn(order)
        whenever(carRepository.findById(car.id.toString())).thenReturn(car)
        whenever(orderRepository.patch(order.id.toString(), requestEntity)).thenReturn(updatedOrder)

        // WHEN
        val result = orderService.patch(order.id.toString(), request)

        // THEN
        assertEquals(response, result)
        verify(orderRepository).patch(order.id.toString(), requestEntity)
    }

    @Test
    fun `patch should return updated order with end date`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val order = randomAggregatedOrder(car, user)
        val price = car.price?.multiply(BigDecimal(29))
        val request = updateOrderRequest().copy(from = null, to = monthAfter)
        val requestEntity = orderPatch(request)
        val updatedOrder = updatedOrder(order, request)
        val response = responseOrder(updatedOrder, car).copy(price = price)
        whenever(orderRepository.findById(order.id.toString())).thenReturn(order)
        whenever(carRepository.findById(car.id.toString())).thenReturn(car)
        whenever(orderRepository.patch(order.id.toString(), requestEntity)).thenReturn(updatedOrder)

        // WHEN
        val result = orderService.patch(order.id.toString(), request)

        // THEN
        assertEquals(response, result)
        verify(orderRepository).patch(order.id.toString(), requestEntity)
    }

    @Test
    fun `patch should throw ResourceNotFoundException if order is not found`() {
        // GIVEN
        val orderId = "unknown"

        // WHEN // THEN
        assertThrows(
            NotFoundException::class.java,
            { orderService.patch(orderId, updateOrderRequest()) }
        )
    }

    @Test
    fun `deleteById should be successful`() {
        // GIVEN
        val orderId = "someId"

        // WHEN // THEN
        assertNotNull(orderService.deleteById(orderId))
        verify(orderRepository).deleteById(orderId)
    }

    @Test
    fun `findByUser should return all user's orders`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val order = randomOrder(car.id, user.id)
        val orderResponse = responseOrder(order, car)
        whenever(orderRepository.findByUserId(user.id.toString())).thenReturn(listOf(order))
        whenever(carRepository.findById(car.id.toString())).thenReturn(car)

        // WHEN
        val orders = orderService.findByUser(user.id.toString())

        // THEN
        assertTrue(orders.isNotEmpty())
        assertTrue(orders.contains(orderResponse))
    }

    @Test
    fun `findByUser should return empty list if user's orders don't exist`() {
        // GIVEN
        val userId = ObjectId().toString()
        whenever(orderRepository.findByUserId(userId)).thenReturn(listOf())

        // WHEN
        val orders = orderService.findByUser(userId)

        // THEN
        assertTrue(orders.isEmpty())
    }

    @Test
    fun `findByCar should return all orders on car`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val order = randomOrder(car.id, user.id)
        val orderResponse = responseOrder(order, car)
        whenever(orderRepository.findByCarId(car.id.toString())).thenReturn(listOf(order))
        whenever(carRepository.findById(car.id.toString())).thenReturn(car)

        // WHEN
        val orders = orderService.findByCar(car.id.toString())

        // THEN
        assertTrue(orders.isNotEmpty())
        assertTrue(orders.contains(orderResponse))
    }

    @Test
    fun `findByCar should return empty list if orders on car don't exist`() {
        // GIVEN
        val carId = ObjectId().toString()
        whenever(orderRepository.findByCarId(carId)).thenReturn(listOf())

        // WHEN
        val orders = orderService.findByCar(carId)

        // THEN
        assertTrue(orders.isEmpty())
    }

    @Test
    fun `findByCarAndUser should return all user's orders on car`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val order = randomOrder(car.id, user.id)
        val orderResponse = responseOrder(order, car)
        whenever(orderRepository.findByCarIdAndUserId(car.id.toString(), user.id.toString())).thenReturn(listOf(order))
        whenever(carRepository.findById(car.id.toString())).thenReturn(car)

        // WHEN
        val orders = orderService.findByCarAndUser(car.id.toString(), user.id.toString())

        // THEN
        assertTrue(orders.isNotEmpty())
        assertTrue(orders.contains(orderResponse))
    }

    @Test
    fun `findByCarAndUser should return empty list if such orders don't exist`() {
        // GIVEN
        val carId = ObjectId().toString()
        val userId = ObjectId().toString()
        whenever(orderRepository.findByCarIdAndUserId(carId, userId)).thenReturn(listOf())

        // WHEN
        val orders = orderService.findByCarAndUser(carId, userId)

        // THEN
        assertTrue(orders.isEmpty())
    }
}
