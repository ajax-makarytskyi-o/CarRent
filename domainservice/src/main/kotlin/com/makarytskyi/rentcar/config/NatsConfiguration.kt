package com.makarytskyi.rentcar.config

import io.nats.client.Connection
import io.nats.client.Nats
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NatsConfiguration {
    @Bean
    fun natsPrimaryConnection(
        @Value("\${nats.spring.io.nats.client.servers}")
        natsUrl: String
    ): Connection = Nats.connect(natsUrl)
}
