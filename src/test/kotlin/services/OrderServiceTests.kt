
package services

import fixtures.CarFixture.carId
import fixtures.OrderFixture.createOrderEntity
import fixtures.OrderFixture.createOrderRequest
import fixtures.OrderFixture.createdOrder
import fixtures.OrderFixture.createdOrderResponse
import fixtures.OrderFixture.existingOrder
import fixtures.OrderFixture.existingOrderOnCar
import fixtures.OrderFixture.orderId
import fixtures.OrderFixture.responseOrder
import fixtures.OrderFixture.updateOrderEntity
import fixtures.OrderFixture.updateOrderRequest
import fixtures.OrderFixture.updatedOrder
import fixtures.OrderFixture.updatedOrderResponse
import fixtures.UserFixture.existingUser
import fixtures.UserFixture.userId
import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.service.OrderService
import fixtures.CarFixture.existingCar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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
    lateinit var orderService: OrderService

    @Test
    fun `getById should return OrderResponse when Order exists`() {
        //GIVEN
        val car = existingCar()
        val user = existingUser()
        val order = existingOrder(car, user)
        val response = responseOrder(order, car)
        whenever(orderRepository.findById(orderId)).thenReturn(order)
        whenever(carRepository.findById(carId)).thenReturn(car)

        //WHEN
        val result = orderService.getById(orderId)

        //THEN
        assertEquals(response, result)
        verify(orderRepository).findById(orderId)
    }

    @Test
    fun `getById should return throw ResourceNotFoundException`() {
        //GIVEN
        whenever(orderRepository.findById(orderId)).thenReturn(null)

        //WHEN //THEN
        assertThrows(ResourceNotFoundException::class.java, { orderService.getById(orderId) })
        verify(orderRepository).findById(orderId)
    }

    @Test
    fun `findAll should return List of OrderResponse`() {
        //GIVEN
        val car = existingCar()
        val user = existingUser()
        val order = existingOrder(car, user)
        val response = responseOrder(order, car)
        val orders = listOf(order)
        val expected = listOf(response)
        whenever(orderRepository.findAll()).thenReturn(orders)
        whenever(carRepository.findById(carId)).thenReturn(car)

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
        Assertions.assertEquals(emptyList<Repairing>(), result)
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
        whenever(carRepository.findById(carId)).thenReturn(car)
        whenever(userRepository.findById(userId)).thenReturn(user)

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
        whenever(userRepository.findById(userId)).thenReturn(user)
        whenever(carRepository.findById(carId)).thenReturn(null)

        //WHEN //THEN
        assertThrows(ResourceNotFoundException::class.java, { orderService.create(createOrderRequest(car, user)) })
    }

    @Test
    fun `should throw IllegalArgumentException if user doesn't exist`() {
        //GIVEN
        val car = existingCar()
        val user = existingUser()
        val request = createOrderRequest(car, user)
        whenever(userRepository.findById(userId)).thenReturn(null)

        //WHEN //THEN
        assertThrows(IllegalArgumentException::class.java, { orderService.create(request) })
    }

    @Test
    fun `should throw IllegalArgumentException if car is ordered`() {
        //GIVEN
        val user = existingUser()
        val car = existingCar()
        whenever(userRepository.findById(userId)).thenReturn(user)
        whenever(carRepository.findById(carId)).thenReturn(car)
        whenever(orderRepository.findByCarId(carId)).thenReturn(listOf(existingOrderOnCar(car, user)))

        //WHEN //THEN
        assertThrows(IllegalArgumentException::class.java, { orderService.create(createOrderRequest(car, user)) })
        verify(orderRepository).findByCarId(carId)
    }

    @Test
    fun `update should return updated order`() {
        //GIVEN
        val car = existingCar()
        val user = existingUser()
        val order = existingOrder(car, user)
        val request = updateOrderRequest()
        val requestEntity = updateOrderEntity(request)
        val updatedOrder = updatedOrder(order, request)
        val response = responseOrder(updatedOrder, car)
        whenever(orderRepository.findById(orderId)).thenReturn(order)
        whenever(carRepository.findById(carId)).thenReturn(car)
        whenever(orderRepository.update(orderId, requestEntity)).thenReturn(updatedOrder)

        //WHEN
        val result = orderService.update(orderId, request)

        //THEN
        assertEquals(response, result)
        verify(orderRepository).update(orderId, requestEntity)
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
}
