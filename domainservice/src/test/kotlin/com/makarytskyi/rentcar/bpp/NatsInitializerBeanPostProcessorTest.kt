package com.makarytskyi.rentcar.bpp

import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.rentcar.controller.nats.order.CreateOrderNatsController
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import io.nats.client.Connection
import io.nats.client.Dispatcher
import io.nats.client.MessageHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class NatsInitializerBeanPostProcessorTest {

    val natsConnection: Connection = spyk()
    val dispatcher: Dispatcher = spyk()

    @MockK
    lateinit var createController: CreateOrderNatsController

    val natInitializer: NatsInitializerBeanPostProcessor = NatsInitializerBeanPostProcessor(dispatcher)

    @Test
    fun `beanPostProcessor should create dispatcher and subscribe on subject if bean is NatsController`() {
        //GIVEN
        val queueGroup = "create_order"
        every { createController.subject } returns CREATE
        every { createController.queueGroup } returns queueGroup
        val messageHandlerSlot = slot<MessageHandler>()
        every { dispatcher.subscribe(any(), any(), capture(messageHandlerSlot)) } returns mockk()

        //WHEN
        natInitializer.postProcessAfterInitialization(createController, "createController")

        //THEN
        verify(exactly = 1) { dispatcher.subscribe(CREATE, queueGroup, any()) }
    }

    @Test
    fun `beanPostProcessor should do nothing if bean is not NatsController`() {
        //GIVEN
        every { createController.subject } returns CREATE
        every { natsConnection.createDispatcher(any()) } returns dispatcher

        //WHEN
        natInitializer.postProcessAfterInitialization(Any(), "anyClass")

        //THEN
        verify(exactly = 0) { natsConnection.createDispatcher(any()) }
        verify(exactly = 0) { dispatcher.subscribe(CREATE, createController.queueGroup) }
    }
}
