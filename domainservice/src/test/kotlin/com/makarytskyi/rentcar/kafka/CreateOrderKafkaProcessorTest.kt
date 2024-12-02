package com.makarytskyi.rentcar.kafka

import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.internalapi.subject.NatsSubject
import com.makarytskyi.rentcar.config.NatsListener
import com.makarytskyi.rentcar.fixtures.CarFixture.randomPrice
import com.makarytskyi.rentcar.fixtures.OrderFixture.orderProto
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import java.time.Duration
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.test.testUsingVirtualTime
import reactor.test.StepVerifierOptions
import reactor.test.scheduler.VirtualTimeScheduler
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@ExtendWith(MockKExtension::class)
class CreateOrderKafkaProcessorTest {

    @MockK
    lateinit var natsPublisher: NatsMessagePublisher

    @MockK
    lateinit var event: KafkaEvent<Order>

    @MockK
    lateinit var listener: NatsListener

    @InjectMockKs
    lateinit var kafkaProcessor: CreateOrderKafkaProcessor

    @Test
    fun `kafka processor should successfully publish if nats is connected`() {
        // GIVEN
        val carId = ObjectId()
        val userId = ObjectId()
        val price = randomPrice()
        val order = orderProto(randomOrder(carId, userId), price.toDouble())
        every { listener.isConnected() } returns true
        every { event.data } returns order
        every { event.ack() } returns Unit

        // WHEN
        kafkaProcessor.handle(event).subscribe()

        // THEN
        verify(exactly = 1) {
            natsPublisher.publish(
                NatsSubject.Order.createOrderOnCar(userId.toString()),
                order
            )
        }
    }

    @Test
    fun `kafka processor should not publish and send acknowledge if nats is disconnected`() {
        // GIVEN
        val carId = ObjectId()
        val userId = ObjectId()
        val price = randomPrice()
        val order = orderProto(randomOrder(carId, userId), price.toDouble())
        every { listener.isConnected() } returns false
        every { event.data } returns order
        every { event.ack() } returns Unit
        val virtualTimeScheduler = VirtualTimeScheduler.getOrSet()

        // WHEN
        val result = kafkaProcessor.handle(event)

        // THEN
        result.testUsingVirtualTime(StepVerifierOptions.create())
            .then { virtualTimeScheduler.advanceTimeBy(Duration.ofSeconds(10)) }
            .thenCancel()
            .verify()

        verify(exactly = 0) { event.ack() }
        verify(exactly = 0) { natsPublisher.publish(any(), any()) }
    }
}
