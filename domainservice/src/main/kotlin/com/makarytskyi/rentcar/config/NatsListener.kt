package com.makarytskyi.rentcar.config

import io.nats.client.Connection
import io.nats.client.ConnectionListener
import org.springframework.stereotype.Component

@Component
class NatsListener : ConnectionListener {
    private var connected: Boolean = false

    override fun connectionEvent(connection: Connection, event: ConnectionListener.Events) {
        connected = (connection.status == Connection.Status.CONNECTED)
    }

    fun isConnected() = connected
}
