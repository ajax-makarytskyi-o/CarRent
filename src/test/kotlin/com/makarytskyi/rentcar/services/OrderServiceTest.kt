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
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.bson.types.ObjectId
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
internal class OrderServiceTest {
    @MockK
    lateinit var orderRepository: OrderRepository

    @MockK
    lateinit var carRepository: CarRepository

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var orderService: OrderServiceImpl

    @Test
    fun `getById should return order response when order exists`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val order = randomAggregatedOrder(car, user)
        val response = responseAggregatedOrder(order, car)
        every { orderRepository.findFullById(order.id.toString()) }.returns(Mono.just(order))

        // WHEN
        val result = orderService.getById(order.id.toString())

        // THEN
        StepVerifier.create(result)
            .assertNext {
                assertEquals(response, it)
            }
            .verifyComplete()

        verify { orderRepository.findFullById(order.id.toString()) }
    }

    @Test
    fun `getById should return throw ResourceNotFoundException`() {
        // GIVEN
        val orderId = ObjectId()
        every { orderRepository.findFullById(orderId.toString()) }.returns(Mono.empty())

        // WHEN // THEN
        StepVerifier.create(orderService.getById(orderId.toString()))
            .verifyError(NotFoundException::class.java)

        verify { orderRepository.findFullById(orderId.toString()) }
    }

    @Test
    fun `findAll should return order responses`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val order = randomAggregatedOrder(car, user)
        val response = responseAggregatedOrder(order, car)
        val orders = listOf(order)
        val expected = listOf(response)
        every { orderRepository.findFullAll(0, 10) }.returns(Flux.fromIterable(orders))

        // WHEN
        val result = orderService.findAll(0, 10)

        // THEN
        StepVerifier.create(result.collectList())
            .assertNext {
                assertTrue(it.containsAll(expected))
            }
            .verifyComplete()

        verify { orderRepository.findFullAll(0, 10) }
    }

    @Test
    fun `findAll should not return anything if repository didn't return anything`() {
        // GIVEN
        every { orderRepository.findFullAll(0, 10) }.returns(Flux.empty())

        // WHEN
        val result = orderService.findAll(0, 10)

        // THEN
        StepVerifier.create(result)
            .verifyComplete()

        verify { orderRepository.findFullAll(0, 10) }
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
        every { orderRepository.create(requestEntity) }.returns(Mono.just(createdOrder))
        every { carRepository.findById(car.id.toString()) }.returns(Mono.just(car))
        every { userRepository.findById(user.id.toString()) }.returns(Mono.just(user))
        every { orderRepository.findByCarId(car.id.toString()) }.returns(Flux.empty())

        // WHEN
        val result = orderService.create(request)

        // THEN
        StepVerifier.create(result)
            .assertNext {
                assertEquals(response, it)
            }
            .verifyComplete()

        verify { orderRepository.create(requestEntity) }
    }

    @Test
    fun `should throw IllegalArgumentException if car doesn't exist`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        every { userRepository.findById(user.id.toString()) }.returns(Mono.just(user))
        every { carRepository.findById(car.id.toString()) }.returns(Mono.empty())

        // WHEN // THEN
        StepVerifier.create(orderService.create(createOrderRequest(car, user)))
            .verifyError(NotFoundException::class.java)
    }

    @Test
    fun `should throw IllegalArgumentException if user doesn't exist`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val request = createOrderRequest(car, user)
        every { userRepository.findById(user.id.toString()) }.returns(Mono.empty())

        // WHEN // THEN
        StepVerifier.create(orderService.create(request))
            .verifyError(IllegalArgumentException::class.java)
    }

    @Test
    fun `should throw IllegalArgumentException if car is ordered`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        every { userRepository.findById(user.id.toString()) }.returns(Mono.just(user))
        every { carRepository.findById(car.id.toString()) }.returns(Mono.just(car))
        every { orderRepository.findByCarId(car.id.toString()) }.returns(
            Flux.just(
                randomOrder(car.id, user.id).copy(
                    from = monthAfter,
                    to = monthAndDayAfter
                )
            )
        )

        // WHEN // THEN
        StepVerifier.create(orderService.create(createOrderRequest(car, user)))
            .verifyError(IllegalArgumentException::class.java)

        verify { orderRepository.findByCarId(car.id.toString()) }
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
        StepVerifier.create(orderService.create(request))
            .verifyError(IllegalArgumentException::class.java)
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
        StepVerifier.create(orderService.create(request))
            .verifyError(IllegalArgumentException::class.java)
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
        every { orderRepository.findFullById(order.id.toString()) }.returns(Mono.just(order))
        every { carRepository.findById(car.id.toString()) }.returns(Mono.just(car))
        every { orderRepository.findByCarId(car.id.toString()) }.returns(Flux.empty())
        every { orderRepository.patch(order.id.toString(), requestEntity) }.returns(Mono.just(updatedOrder))

        // WHEN
        val result = orderService.patch(order.id.toString(), request)

        // THEN
        StepVerifier.create(result)
            .assertNext {
                assertEquals(response, it)
            }
            .verifyComplete()

        verify { orderRepository.patch(order.id.toString(), requestEntity) }
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
        every { orderRepository.findFullById(order.id.toString()) }.returns(Mono.just(order))
        every { carRepository.findById(car.id.toString()) }.returns(Mono.just(car))
        every { orderRepository.findByCarId(car.id.toString()) }.returns(Flux.empty())
        every { orderRepository.patch(order.id.toString(), requestEntity) }.returns(Mono.just(updatedOrder))

        // WHEN
        val result = orderService.patch(order.id.toString(), request)

        // THEN
        StepVerifier.create(result)
            .assertNext {
                assertEquals(response, it)
            }
            .verifyComplete()

        verify { orderRepository.patch(order.id.toString(), requestEntity) }
    }

    @Test
    fun `patch should throw ResourceNotFoundException if order is not found`() {
        // GIVEN
        val orderId = "unknown"
        every { orderRepository.findFullById(orderId) }.returns(Mono.empty())

        // WHEN // THEN
        StepVerifier.create(orderService.patch(orderId, updateOrderRequest()))
            .verifyError(NotFoundException::class.java)
    }

    @Test
    fun `deleteById should be successful`() {
        // GIVEN
        val orderId = "someId"
        every { orderRepository.deleteById(orderId) }.returns(Mono.empty())

        // WHEN // THEN
        StepVerifier.create(orderService.deleteById(orderId))
            .verifyComplete()

        verify { orderRepository.deleteById(orderId) }
    }

    @Test
    fun `findByUser should return all user's orders`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val order = randomOrder(car.id, user.id)
        val orderResponse = responseOrder(order, car)
        every { orderRepository.findByUserId(user.id.toString()) }.returns(Flux.just(order))
        every { carRepository.findById(car.id.toString()) }.returns(Mono.just(car))

        // WHEN
        val orders = orderService.findByUser(user.id.toString())

        // THEN
        StepVerifier.create(orders.collectList())
            .assertNext {
                assertTrue(it.contains(orderResponse))
            }
            .verifyComplete()
    }

    @Test
    fun `findByUser should not return anything if user's orders don't exist`() {
        // GIVEN
        val userId = ObjectId().toString()
        every { orderRepository.findByUserId(userId) }.returns(Flux.empty())

        // WHEN
        val orders = orderService.findByUser(userId)

        // THEN
        StepVerifier.create(orders)
            .verifyComplete()
    }

    @Test
    fun `findByCar should return all orders on car`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val order = randomOrder(car.id, user.id)
        val orderResponse = responseOrder(order, car)
        every { orderRepository.findByCarId(car.id.toString()) }.returns(Flux.just(order))
        every { carRepository.findById(car.id.toString()) }.returns(Mono.just(car))

        // WHEN
        val orders = orderService.findByCar(car.id.toString())

        // THEN
        StepVerifier.create(orders.collectList())
            .assertNext {
                assertTrue(it.contains(orderResponse))
            }
            .verifyComplete()
    }

    @Test
    fun `findByCar should not return anything if orders on car don't exist`() {
        // GIVEN
        val carId = ObjectId().toString()
        every { orderRepository.findByCarId(carId) }.returns(Flux.empty())

        // WHEN
        val orders = orderService.findByCar(carId)

        // THEN
        StepVerifier.create(orders)
            .verifyComplete()
    }

    @Test
    fun `findByCarAndUser should return all user's orders on car`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val order = randomOrder(car.id, user.id)
        val orderResponse = responseOrder(order, car)
        every { orderRepository.findByCarIdAndUserId(car.id.toString(), user.id.toString()) }.returns(Flux.just(order))
        every { carRepository.findById(car.id.toString()) }.returns(Mono.just(car))

        // WHEN
        val orders = orderService.findByCarAndUser(car.id.toString(), user.id.toString())

        // THEN
        StepVerifier.create(orders.collectList())
            .assertNext {
                assertTrue(it.contains(orderResponse))
            }
            .verifyComplete()
    }

    @Test
    fun `findByCarAndUser should not return anything if such orders don't exist`() {
        // GIVEN
        val carId = ObjectId().toString()
        val userId = ObjectId().toString()
        every { orderRepository.findByCarIdAndUserId(carId, userId) }.returns(Flux.empty())

        // WHEN
        val orders = orderService.findByCarAndUser(carId, userId)

        // THEN
        StepVerifier.create(orders)
            .verifyComplete()
    }
}
