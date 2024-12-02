package com.makarytskyi.rentcar.common.config

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.nats.client.Connection
import io.nats.client.ConnectionListener
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class NatsListenerTest {
    @MockK
    lateinit var connection: Connection

    @InjectMockKs
    lateinit var natsListener: NatsListener

    @Test
    fun `listener should return false if status of connection is not connected`() {
        // GIVEN
        every { connection.status } returns Connection.Status.CONNECTED

        // WHEN
        natsListener.connectionEvent(connection, ConnectionListener.Events.CONNECTED)

        // THEN
        assertTrue(natsListener.isConnected(), "isConnected should return true if connection status is connected")
    }

    @Test
    fun `listener should return true if status of connection is connected`() {
        // GIVEN
        every { connection.status } returns Connection.Status.DISCONNECTED

        // WHEN
        natsListener.connectionEvent(connection, ConnectionListener.Events.DISCONNECTED)

        // THEN
        assertFalse(natsListener.isConnected(), "isConnected should return false if connection status is connected")

    }
}
