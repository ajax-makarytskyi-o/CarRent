package com.makarytskyi.rentcar.kafka

import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class TestConfiguration(
    private val kafkaProperties: KafkaProperties,
) {
    @Bean
    fun consumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, ByteArray> =
        DefaultKafkaConsumerFactory(
            kafkaProperties.buildConsumerProperties(null),
            StringDeserializer(),
            ByteArrayDeserializer()
        )

    @Bean
    fun admin(kafkaAdmin: KafkaAdmin): Admin = Admin.create(kafkaAdmin.configurationProperties)
}
