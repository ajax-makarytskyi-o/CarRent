package com.makarytskyi.rentcar.services

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderEntity
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderRequestDto
import com.makarytskyi.rentcar.fixtures.OrderFixture.createdOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAndDayAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.orderPatch
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomAggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseAggregatedOrderDto
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseOrderDto
import com.makarytskyi.rentcar.fixtures.OrderFixture.tomorrow
import com.makarytskyi.rentcar.fixtures.OrderFixture.updateOrderRequestDto
import com.makarytskyi.rentcar.fixtures.OrderFixture.updatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.yesterday
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.kafka.CreateOrderKafkaProducer
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
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

@ExtendWith(MockKExtension::class)
internal class OrderServiceTest {

    @MockK
    lateinit var orderCreateOrderKafkaProducer: CreateOrderKafkaProducer

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
        val response = responseAggregatedOrderDto(order, car)
        every { orderRepository.findFullById(order.id.toString()) } returns order.toMono()

        // WHEN
        val result = orderService.getById(order.id.toString())

        // THEN
        result
            .test()
            .expectNext(response)
            .verifyComplete()

        verify { orderRepository.findFullById(order.id.toString()) }
    }

    @Test
    fun `getById should return NotFoundException`() {
        // GIVEN
        val orderId = ObjectId()
        every { orderRepository.findFullById(orderId.toString()) } returns Mono.empty()

        // WHEN // THEN
        orderService.getById(orderId.toString())
            .test()
            .verifyError<NotFoundException>()

        verify { orderRepository.findFullById(orderId.toString()) }
    }

    @Test
    fun `findAll should return order responses`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val order = randomAggregatedOrder(car, user)
        val response = responseAggregatedOrderDto(order, car)
        val orders = listOf(order)
        every { orderRepository.findFullAll(0, 10) } returns orders.toFlux()

        // WHEN
        val result = orderService.findAll(0, 10)

        // THEN
        result.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(response)
            }
            .verifyComplete()

        verify { orderRepository.findFullAll(0, 10) }
    }

    @Test
    fun `findAll should return empty if repository returned empty`() {
        // GIVEN
        every { orderRepository.findFullAll(0, 10) } returns Flux.empty()

        // WHEN
        val result = orderService.findAll(0, 10)

        // THEN
        result
            .test()
            .verifyComplete()

        verify { orderRepository.findFullAll(0, 10) }
    }

    @Test
    fun `should create order successfully`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val request = createOrderRequestDto(car, user)
        val requestEntity = createOrderEntity(request)
        val createdOrder = createdOrder(requestEntity)
        val response = responseOrderDto(createdOrder, car)
        every { orderRepository.create(requestEntity) } returns createdOrder.toMono()
        every { carRepository.findById(car.id.toString()) } returns car.toMono()
        every { userRepository.findById(user.id.toString()) } returns user.toMono()
        every { orderRepository.findByCarId(car.id.toString()) } returns Flux.empty()
        every { orderCreateOrderKafkaProducer.sendCreateRepairing(any()) } returns Mono.empty()

        // WHEN
        val result = orderService.create(request)

        // THEN
        result
            .test()
            .expectNext(response)
            .verifyComplete()

        verify { orderRepository.create(requestEntity) }
    }

    @Test
    fun `should return IllegalArgumentException if car doesn't exist`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        every { userRepository.findById(user.id.toString()) } returns user.toMono()
        every { carRepository.findById(car.id.toString()) } returns Mono.empty()

        // WHEN // THEN
        orderService.create(createOrderRequestDto(car, user))
            .test()
            .verifyError<NotFoundException>()

        verify {
            userRepository.findById(user.id.toString())
            carRepository.findById(car.id.toString())
        }
    }

    @Test
    fun `should return NotFoundException if user doesn't exist`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val request = createOrderRequestDto(car, user)
        every { userRepository.findById(user.id.toString()) } returns Mono.empty()
        every { carRepository.findById(car.id.toString()) } returns car.toMono()

        // WHEN // THEN
        orderService.create(request)
            .test()
            .verifyError<NotFoundException>()

        verify { userRepository.findById(user.id.toString()) }
    }

    @Test
    fun `should return IllegalArgumentException if car is ordered`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val orders = listOf(
            randomOrder(car.id, user.id).copy(
                from = monthAfter,
                to = monthAndDayAfter
            )
        )
        every { userRepository.findById(user.id.toString()) } returns user.toMono()
        every { carRepository.findById(car.id.toString()) } returns car.toMono()
        every { orderRepository.findByCarId(car.id.toString()) } returns orders.toFlux()

        // WHEN // THEN
        orderService.create(createOrderRequestDto(car, user))
            .test()
            .verifyError<IllegalArgumentException>()

        verify { orderRepository.findByCarId(car.id.toString()) }
    }

    @Test
    fun `should return IllegalArgumentException if dates reversed`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val request = createOrderRequestDto(car, user).copy(
            from = monthAfter,
            to = tomorrow
        )

        // WHEN // THEN
        orderService.create(request)
            .test()
            .verifyError<IllegalArgumentException>()
    }

    @Test
    fun `should return IllegalArgumentException if date in past`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val request = createOrderRequestDto(car, user).copy(
            from = yesterday,
            to = tomorrow
        )

        // WHEN // THEN
        orderService.create(request)
            .test()
            .verifyError<IllegalArgumentException>()
    }

    @Test
    fun `patch should return updated order with start date`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val order = randomAggregatedOrder(car, user)
        val price = car.price?.multiply(BigDecimal(1))
        val request = updateOrderRequestDto().copy(from = tomorrow, to = null)
        val requestEntity = orderPatch(request)
        val updatedOrder = updatedOrder(order, request)
        val response = responseOrderDto(updatedOrder, car).copy(price = price!!)
        every { orderRepository.findFullById(order.id.toString()) } returns order.toMono()
        every { carRepository.findById(car.id.toString()) } returns car.toMono()
        every { orderRepository.findByCarId(car.id.toString()) } returns Flux.empty()
        every { orderRepository.patch(order.id.toString(), requestEntity) } returns updatedOrder.toMono()

        // WHEN
        val result = orderService.patch(order.id.toString(), request)

        // THEN
        result
            .test()
            .expectNext(response)
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
        val request = updateOrderRequestDto().copy(from = null, to = monthAfter)
        val requestEntity = orderPatch(request)
        val updatedOrder = updatedOrder(order, request)
        val response = responseOrderDto(updatedOrder, car).copy(price = price!!)
        every { orderRepository.findFullById(order.id.toString()) } returns order.toMono()
        every { carRepository.findById(car.id.toString()) } returns car.toMono()
        every { orderRepository.findByCarId(car.id.toString()) } returns Flux.empty()
        every { orderRepository.patch(order.id.toString(), requestEntity) } returns updatedOrder.toMono()

        // WHEN
        val result = orderService.patch(order.id.toString(), request)

        // THEN
        result
            .test()
            .expectNext(response)
            .verifyComplete()

        verify { orderRepository.patch(order.id.toString(), requestEntity) }
    }

    @Test
    fun `patch should return NotFoundException if order is not found`() {
        // GIVEN
        val orderId = "unknown"
        every { orderRepository.findFullById(orderId) } returns Mono.empty()

        // WHEN // THEN
        orderService.patch(orderId, updateOrderRequestDto())
            .test()
            .verifyError<NotFoundException>()

        verify { orderRepository.findFullById(orderId) }
    }

    @Test
    fun `deleteById should be successful`() {
        // GIVEN
        val orderId = "someId"
        every { orderRepository.deleteById(orderId) } returns Mono.empty()

        // WHEN // THEN
        orderService.deleteById(orderId)
            .test()
            .verifyComplete()

        verify { orderRepository.deleteById(orderId) }
    }

    @Test
    fun `findByUser should return all user's orders`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val order = randomOrder(car.id, user.id)
        val orders = listOf(order)
        val orderResponse = responseOrderDto(order, car)
        every { orderRepository.findByUserId(user.id.toString()) } returns orders.toFlux()
        every { carRepository.findById(car.id.toString()) } returns car.toMono()

        // WHEN
        val foundOrders = orderService.findByUser(user.id.toString())

        // THEN
        foundOrders.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(orderResponse)
            }
            .verifyComplete()

        verify { orderRepository.findByUserId(user.id.toString()) }
    }

    @Test
    fun `findByUser should return empty if user's orders don't exist`() {
        // GIVEN
        val userId = ObjectId().toString()
        every { orderRepository.findByUserId(userId) } returns Flux.empty()

        // WHEN
        val orders = orderService.findByUser(userId)

        // THEN
        orders
            .test()
            .verifyComplete()

        verify { orderRepository.findByUserId(userId) }
    }

    @Test
    fun `findByCar should return all orders on car`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val order = randomOrder(car.id, user.id)
        val orders = listOf(order)
        val orderResponse = responseOrderDto(order, car)
        every { orderRepository.findByCarId(car.id.toString()) } returns orders.toFlux()
        every { carRepository.findById(car.id.toString()) } returns car.toMono()

        // WHEN
        val foundOrders = orderService.findByCar(car.id.toString())

        // THEN
        foundOrders.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(orderResponse)
            }
            .verifyComplete()

        verify { orderRepository.findByCarId(car.id.toString()) }
    }

    @Test
    fun `findByCar should return empty if orders on car don't exist`() {
        // GIVEN
        val carId = ObjectId().toString()
        every { orderRepository.findByCarId(carId) } returns Flux.empty()

        // WHEN
        val orders = orderService.findByCar(carId)

        // THEN
        orders
            .test()
            .verifyComplete()

        verify { orderRepository.findByCarId(carId) }
    }

    @Test
    fun `findByCarAndUser should return all user's orders on car`() {
        // GIVEN
        val user = randomUser()
        val car = randomCar()
        val order = randomOrder(car.id, user.id)
        val orders = listOf(order)
        val orderResponse = responseOrderDto(order, car)
        every { orderRepository.findByCarIdAndUserId(car.id.toString(), user.id.toString()) } returns orders.toFlux()
        every { carRepository.findById(car.id.toString()) } returns car.toMono()

        // WHEN
        val foundOrders = orderService.findByCarAndUser(car.id.toString(), user.id.toString())

        // THEN
        foundOrders.collectList()
            .test()
            .assertNext {
                assertThat(it).contains(orderResponse)
            }
            .verifyComplete()

        verify { orderRepository.findByCarIdAndUserId(car.id.toString(), user.id.toString()) }
    }

    @Test
    fun `findByCarAndUser should return empty if such orders don't exist`() {
        // GIVEN
        val carId = ObjectId().toString()
        val userId = ObjectId().toString()
        every { orderRepository.findByCarIdAndUserId(carId, userId) } returns Flux.empty()

        // WHEN
        val orders = orderService.findByCarAndUser(carId, userId)

        // THEN
        orders
            .test()
            .verifyComplete()

        verify { orderRepository.findByCarIdAndUserId(carId, userId) }
    }
}
