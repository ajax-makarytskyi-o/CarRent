package com.makarytskyi.rentcar.order.infrastructure.kafka

import com.makarytskyi.commonmodels.order.OrderCancellationNotification
import com.makarytskyi.internalapi.topic.KafkaTopic
import com.makarytskyi.rentcar.car.application.port.output.CarRepositoryOutputPort
import com.makarytskyi.rentcar.fixtures.CarFixture.createCarRequest
import com.makarytskyi.rentcar.fixtures.NotificationFixture.notification
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderRequest
import com.makarytskyi.rentcar.fixtures.OrderFixture.threeDaysAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.tomorrow
import com.makarytskyi.rentcar.fixtures.OrderFixture.twoDaysAfter
import com.makarytskyi.rentcar.fixtures.RepairingFixture.createRepairingRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.createUserRequest
import com.makarytskyi.rentcar.order.ContainerBase
import com.makarytskyi.rentcar.order.application.port.input.OrderServiceInputPort
import com.makarytskyi.rentcar.repairing.application.port.input.RepairingServiceInputPort
import com.makarytskyi.rentcar.user.application.port.output.UserRepositoryOutputPort
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import systems.ajax.kafka.mock.KafkaMockExtension

class KafkaIntegrationTest : ContainerBase {
    @Autowired
    private lateinit var carRepository: CarRepositoryOutputPort

    @Autowired
    private lateinit var userRepository: UserRepositoryOutputPort

    @Autowired
    private lateinit var orderService: OrderServiceInputPort

    @Autowired
    private lateinit var repairingService: RepairingServiceInputPort

    @Test
    fun `notification should be in kafka topic after repairing cancelling order was created`() {
        // GIVEN
        val car = carRepository.create(createCarRequest()).block()!!
        val user = userRepository.create(createUserRequest()).block()!!
        val order = orderService
            .create(createOrderRequest(car.id, user.id).copy(from = tomorrow, to = threeDaysAfter)).block()!!

        val repairingRequest = createRepairingRequest(car.id).copy(date = twoDaysAfter)
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
