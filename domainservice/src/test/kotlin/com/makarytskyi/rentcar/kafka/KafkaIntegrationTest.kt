package com.makarytskyi.rentcar.kafka

import com.makarytskyi.commonmodels.order.OrderCancellationUserNotification
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
import java.util.concurrent.TimeUnit
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import reactor.kafka.receiver.KafkaReceiver

class KafkaIntegrationTest : ContainerBase {
    @Autowired
    private lateinit var carRepository: CarRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var repairingService: RepairingService

    @Qualifier("notificationReceiver")
    @Autowired
    private lateinit var notificationReceiver: KafkaReceiver<String, ByteArray>

    @Test
    fun `notification should be in kafka topic after repairing cancelling order was created`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()!!
        val user = userRepository.create(randomUser()).block()!!
        val order =
            orderService.create(createOrderRequestDto(car, user).copy(from = tomorrow, to = threeDaysAfter)).block()!!
        val notificationList = mutableListOf<OrderCancellationUserNotification>()
        val repairingRequest = createRepairingRequest(car).copy(date = twoDaysAfter)
        val expectedNotification = notification(order)

        notificationReceiver.receive()
            .map {
                notificationList.add(OrderCancellationUserNotification.parser().parseFrom(it.value()))
            }
            .subscribe()

        // WHEN
        repairingService.create(repairingRequest).block()!!

        // THEN
        await()
            .atMost(15, TimeUnit.SECONDS)
            .until { notificationList.contains(expectedNotification) }
    }
}
