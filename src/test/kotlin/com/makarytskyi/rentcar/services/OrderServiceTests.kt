
package com.makarytskyi.rentcar.services

import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.service.impl.OrderServiceImpl
import fixtures.CarFixture.carId
import fixtures.CarFixture.existingCar
import fixtures.OrderFixture.createOrderEntity
import fixtures.OrderFixture.createOrderRequest
import fixtures.OrderFixture.createdOrder
import fixtures.OrderFixture.existingAggregatedOrder
import fixtures.OrderFixture.existingOrderOnCar
import fixtures.OrderFixture.monthAfter
import fixtures.OrderFixture.orderId
import fixtures.OrderFixture.responseAggregatedOrder
import fixtures.OrderFixture.responseOrder
import fixtures.OrderFixture.tommorow
import fixtures.OrderFixture.updateOrderEntity
import fixtures.OrderFixture.updateOrderRequest
import fixtures.OrderFixture.updatedOrder
import fixtures.OrderFixture.yesterday
import fixtures.UserFixture.existingUser
import fixtures.UserFixture.userId
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
internal class OrderServiceTests {
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
        //GIVEN
        val car = existingCar()
        val user = existingUser()
        val order = existingAggregatedOrder(car, user)
        val response = responseAggregatedOrder(order, car)
        whenever(orderRepository.findById(orderId.toString())).thenReturn(order)

        //WHEN
        val result = orderService.getById(orderId.toString())

        //THEN
        assertEquals(response, result)
        verify(orderRepository).findById(orderId.toString())
    }

    @Test
    fun `getById should return throw ResourceNotFoundException`() {
        //GIVEN
        whenever(orderRepository.findById(orderId.toString())).thenReturn(null)

        //WHEN //THEN
        assertThrows(ResourceNotFoundException::class.java, { orderService.getById(orderId.toString()) })
        verify(orderRepository).findById(orderId.toString())
    }

    @Test
    fun `findAll should return List of OrderResponse`() {
        //GIVEN
        val car = existingCar()
        val user = existingUser()
        val order = existingAggregatedOrder(car, user)
        val response = responseAggregatedOrder(order, car)
        val orders = listOf(order)
        val expected = listOf(response)
        whenever(orderRepository.findAll()).thenReturn(orders)

        //WHEN
        val result = orderService.findAll()

        //THEN
        assertEquals(expected, result)
        verify(orderRepository).findAll()
    }

    @Test
    fun `findAll should return empty List of OrderResponse if repository return empty List`() {
        //GIVEN
        whenever(orderRepository.findAll()).thenReturn(emptyList())

        //WHEN
        val result = orderService.findAll()

        //THEN
        Assertions.assertEquals(emptyList<MongoRepairing>(), result)
        verify(orderRepository).findAll()
    }

    @Test
    fun `should create order successfully`() {
        //GIVEN
        val car = existingCar()
        val user = existingUser()
        val request = createOrderRequest(car, user)
        val requestEntity = createOrderEntity(request)
        val createdOrder = createdOrder(requestEntity)
        val response = responseOrder(createdOrder, car)
        whenever(orderRepository.create(requestEntity)).thenReturn(createdOrder)
        whenever(carRepository.findById(carId.toString())).thenReturn(car)
        whenever(userRepository.findById(userId.toString())).thenReturn(user)

        //WHEN
        val result = orderService.create(request)

        //THEN
        assertEquals(response, result)
        verify(orderRepository).create(requestEntity)
    }

    @Test
    fun `should throw IllegalArgumentException if car doesn't exist`() {
        //GIVEN
        val user = existingUser()
        val car = existingCar()
        whenever(userRepository.findById(userId.toString())).thenReturn(user)
        whenever(carRepository.findById(carId.toString())).thenReturn(null)

        //WHEN //THEN
        assertThrows(ResourceNotFoundException::class.java, { orderService.create(createOrderRequest(car, user)) })
    }

    @Test
    fun `should throw IllegalArgumentException if user doesn't exist`() {
        //GIVEN
        val car = existingCar()
        val user = existingUser()
        val request = createOrderRequest(car, user)
        whenever(userRepository.findById(userId.toString())).thenReturn(null)

        //WHEN //THEN
        assertThrows(IllegalArgumentException::class.java, { orderService.create(request) })
    }

    @Test
    fun `should throw IllegalArgumentException if car is ordered`() {
        //GIVEN
        val user = existingUser()
        val car = existingCar()
        whenever(userRepository.findById(userId.toString())).thenReturn(user)
        whenever(carRepository.findById(carId.toString())).thenReturn(car)
        whenever(orderRepository.findByCarId(carId.toString())).thenReturn(listOf(existingOrderOnCar(car, user)))

        //WHEN //THEN
        assertThrows(IllegalArgumentException::class.java, { orderService.create(createOrderRequest(car, user)) })
        verify(orderRepository).findByCarId(carId.toString())
    }

    @Test
    fun `should throw IllegalArgumentException if dates reversed`() {
        //GIVEN
        val user = existingUser()
        val car = existingCar()
        val request = createOrderRequest(car, user).copy(
            from = Date.from(monthAfter.toInstant()),
            to = Date.from(tommorow.toInstant())
        )

        //WHEN //THEN
        assertThrows(IllegalArgumentException::class.java, { orderService.create(request) })
    }

    @Test
    fun `should throw IllegalArgumentException if date in past`() {
        //GIVEN
        val user = existingUser()
        val car = existingCar()
        val request = createOrderRequest(car, user).copy(
            from = Date.from(yesterday.toInstant()),
            to = Date.from(tommorow.toInstant())
        )

        //WHEN //THEN
        assertThrows(IllegalArgumentException::class.java, { orderService.create(request) })
    }

    @Test
    fun `update should return updated order with start date`() {
        //GIVEN
        val car = existingCar()
        val user = existingUser()
        val order = existingAggregatedOrder(car, user)
        val price = car.price?.toLong()?.times(2)
        val request = updateOrderRequest().copy(from = Date.from(tommorow.toInstant()), to = null)
        val requestEntity = updateOrderEntity(request)
        val updatedOrder = updatedOrder(order, request)
        val response = responseOrder(updatedOrder, car).copy(price = price)
        whenever(orderRepository.findById(orderId.toString())).thenReturn(order)
        whenever(carRepository.findById(carId.toString())).thenReturn(car)
        whenever(orderRepository.update(orderId.toString(), requestEntity)).thenReturn(updatedOrder)

        //WHEN
        val result = orderService.update(orderId.toString(), request)

        //THEN
        assertEquals(response, result)
        verify(orderRepository).update(orderId.toString(), requestEntity)
    }

    @Test
    fun `update should return updated order with end date`() {
        //GIVEN
        val car = existingCar()
        val user = existingUser()
        val order = existingAggregatedOrder(car, user)
        val price = car.price?.toLong()?.times(28)
        val request = updateOrderRequest().copy(from = null, to = Date.from(monthAfter.toInstant()))
        val requestEntity = updateOrderEntity(request)
        val updatedOrder = updatedOrder(order, request)
        val response = responseOrder(updatedOrder, car).copy(price = price)
        whenever(orderRepository.findById(orderId.toString())).thenReturn(order)
        whenever(carRepository.findById(carId.toString())).thenReturn(car)
        whenever(orderRepository.update(orderId.toString(), requestEntity)).thenReturn(updatedOrder)

        //WHEN
        val result = orderService.update(orderId.toString(), request)

        //THEN
        assertEquals(response, result)
        verify(orderRepository).update(orderId.toString(), requestEntity)
    }

    @Test
    fun `update should throw ResourceNotFoundException if order is not found`() {
        //GIVEN
        val orderId = "unknown"

        //WHEN //THEN
        assertThrows(
            ResourceNotFoundException::class.java,
            { orderService.update(orderId, updateOrderRequest()) })
    }

    @Test
    fun `deleteById should be successful`() {
        //GIVEN
        val orderId = "someId"

        //WHEN //THEN
        assertNotNull(orderService.deleteById(orderId))
        verify(orderRepository).deleteById(orderId)
    }

    @Test
    fun `findByUser should return all user's orders`() {
        //GIVEN
        val user = existingUser()
        val car = existingCar()
        val order = existingOrderOnCar(car, user)
        val orderResponse = responseOrder(order, car)
        whenever(orderRepository.findByUserId(userId.toString())).thenReturn(listOf(order))
        whenever(carRepository.findById(carId.toString())).thenReturn(car)

        //WHEN
        val orders = orderService.findByUser(user.id.toString())

        //THEN
        assertTrue(orders.isNotEmpty())
        assertTrue(orders.contains(orderResponse))
    }

    @Test
    fun `findByUser should return empty list if user's orders don't exist`() {
        //GIVEN
        val userId = ObjectId().toString()
        whenever(orderRepository.findByUserId(userId)).thenReturn(listOf())

        //WHEN
        val orders = orderService.findByUser(userId)

        //THEN
        assertTrue(orders.isEmpty())
    }

    @Test
    fun `findByCar should return all orders on car`() {
        //GIVEN
        val user = existingUser()
        val car = existingCar()
        val order = existingOrderOnCar(car, user)
        val orderResponse = responseOrder(order, car)
        whenever(orderRepository.findByCarId(carId.toString())).thenReturn(listOf(order))
        whenever(carRepository.findById(carId.toString())).thenReturn(car)

        //WHEN
        val orders = orderService.findByCar(carId.toString())

        //THEN
        assertTrue(orders.isNotEmpty())
        assertTrue(orders.contains(orderResponse))
    }

    @Test
    fun `findByCar should return empty list if orders on car don't exist`() {
        //GIVEN
        val carId = ObjectId().toString()
        whenever(orderRepository.findByCarId(carId)).thenReturn(listOf())

        //WHEN
        val orders = orderService.findByCar(carId)

        //THEN
        assertTrue(orders.isEmpty())
    }

    @Test
    fun `findByCarAndUser should return all user's orders on car`() {
        //GIVEN
        val user = existingUser()
        val car = existingCar()
        val order = existingOrderOnCar(car, user)
        val orderResponse = responseOrder(order, car)
        whenever(orderRepository.findByCarIdAndUserId(carId.toString(), userId.toString())).thenReturn(listOf(order))
        whenever(carRepository.findById(carId.toString())).thenReturn(car)

        //WHEN
        val orders = orderService.findByCarAndUser(carId.toString(), userId.toString())

        //THEN
        assertTrue(orders.isNotEmpty())
        assertTrue(orders.contains(orderResponse))
    }

    @Test
    fun `findByCarAndUser should return empty list if such orders don't exist`() {
        //GIVEN
        val carId = ObjectId().toString()
        val userId = ObjectId().toString()
        whenever(orderRepository.findByCarIdAndUserId(carId, userId)).thenReturn(listOf())

        //WHEN
        val orders = orderService.findByCarAndUser(carId, userId)

        //THEN
        assertTrue(orders.isEmpty())
    }
}
