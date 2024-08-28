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
import fixtures.RepairingFixture.existingCar
import fixtures.UserFixture.existingUser
import fixtures.UserFixture.userId
import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.model.Repairing
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.service.OrderService
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
class OrderServiceTests {
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
        whenever(orderRepository.findById(orderId)).thenReturn(existingOrder)
        whenever(carRepository.findById(carId)).thenReturn(existingCar)

        //WHEN
        val result = orderService.getById(orderId)

        //THEN
        assertEquals(responseOrder, result)
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
        val orders = listOf(existingOrder)
        val expected = listOf(responseOrder)
        whenever(orderRepository.findAll()).thenReturn(orders)
        whenever(carRepository.findById(carId)).thenReturn(existingCar)

        //WHEN
        val result = orderService.findAll()

        //THEN
        verify(orderRepository).findAll()
        assertEquals(expected, result)
    }

    @Test
    fun `findAll should return empty List of OrderResponse if repository return empty List`() {
        //GIVEN
        whenever(orderRepository.findAll()).thenReturn(emptyList())

        //WHEN
        val result = orderService.findAll()

        //THEN
        verify(orderRepository).findAll()
        Assertions.assertEquals(emptyList<Repairing>(), result)
    }

    @Test
    fun `should create order successfully`() {
        //GIVEN
        whenever(orderRepository.create(createOrderEntity)).thenReturn(createdOrder)
        whenever(carRepository.findById(carId)).thenReturn(existingCar)
        whenever(userRepository.findById(userId)).thenReturn(existingUser)

        //WHEN
        val result = orderService.create(createOrderRequest)

        //THEN
        verify(orderRepository).create(createOrderEntity)
        assertEquals(createdOrderResponse, result)
    }

    @Test
    fun `should throw IllegalArgumentException if car doesn't exist`() {
        //GIVEN
        whenever(userRepository.findById(userId)).thenReturn(existingUser)
        whenever(carRepository.findById(carId)).thenReturn(null)

        //WHEN //THEN
        assertThrows(ResourceNotFoundException::class.java, { orderService.create(createOrderRequest) })
    }

    @Test
    fun `should throw IllegalArgumentException if user doesn't exist`() {
        //GIVEN
        whenever(userRepository.findById(userId)).thenReturn(null)

        //WHEN //THEN
        assertThrows(IllegalArgumentException::class.java, { orderService.create(createOrderRequest) })
    }

    @Test
    fun `should throw IllegalArgumentException if car is ordered`() {
        //GIVEN
        whenever(userRepository.findById(userId)).thenReturn(existingUser)
        whenever(carRepository.findById(carId)).thenReturn(existingCar)
        whenever(orderRepository.findByCarId(carId)).thenReturn(listOf(existingOrderOnCar))

        //WHEN //THEN
        assertThrows(IllegalArgumentException::class.java, { orderService.create(createOrderRequest) })
        verify(orderRepository).findByCarId(carId)
    }

    @Test
    fun `update should return updated order`() {
        //GIVEN
        whenever(orderRepository.findById(orderId)).thenReturn(existingOrder)
        whenever(carRepository.findById(carId)).thenReturn(existingCar)
        whenever(orderRepository.update(orderId, updateOrderEntity)).thenReturn(updatedOrder)

        //WHEN
        val result = orderService.update(orderId, updateOrderRequest)

        //THEN
        assertEquals(updatedOrderResponse, result)
        verify(orderRepository).update(orderId, updateOrderEntity)
    }

    @Test
    fun `update should throw ResourceNotFoundException if order is not found`() {
        //GIVEN
        val orderId = "unknown"

        //WHEN //THEN
        assertThrows(
            ResourceNotFoundException::class.java,
            { orderService.update(orderId, updateOrderRequest) })
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
