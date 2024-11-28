package com.makarytskyi.rentcar.kafka

import com.makarytskyi.commonmodels.order.OrderCancellationNotification
import com.makarytskyi.internalapi.topic.KafkaTopic
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.NotificationFixture.notification
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderRequestDto
import com.makarytskyi.rentcar.fixtures.OrderFixture.threeDaysAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.tomorrow
import com.makarytskyi.rentcar.fixtures.OrderFixture.twoDaysAfter
import com.makarytskyi.rentcar.fixtures.RepairingFixture.createRepairingRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.ContainerBase
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.service.OrderService
import com.makarytskyi.rentcar.service.RepairingService
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import systems.ajax.kafka.mock.KafkaMockExtension

class KafkaIntegrationTest : ContainerBase {
    @Autowired
    private lateinit var carRepository: CarRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var repairingService: RepairingService

    @Test
    fun `notification should be in kafka topic after repairing cancelling order was created`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()!!
        val user = userRepository.create(randomUser()).block()!!
        val order =
            orderService.create(createOrderRequestDto(car, user).copy(from = tomorrow, to = threeDaysAfter)).block()!!
        val repairingRequest = createRepairingRequest(car).copy(date = twoDaysAfter)
        val expectedNotification = notification(order)

        // WHEN
        repairingService.create(repairingRequest).block()!!

        // THEN
        val provider = kafkaMock.listen<OrderCancellationNotification>(
            KafkaTopic.User.NOTIFICATION,
            OrderCancellationNotification.parser()
        )

        val notification = provider.awaitFirst({ it == expectedNotification })

        assertNotNull(notification, "Notification in kafka topic should be non-null")
    }

    companion object {
        @JvmField
        @RegisterExtension
        val kafkaMock = KafkaMockExtension()
    }
}
