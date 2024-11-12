package com.makarytskyi.rentcar.kafka

import com.makarytskyi.internalapi.subject.KafkaTopic
import java.util.UUID
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions

@Configuration
class TestConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}") private val bootstrapServers: String,
    private val kafkaProperties: KafkaProperties,
) {
    @Bean("testReceiver")
    fun testKafkaReceiver(): KafkaReceiver<String, ByteArray> {
        val options = ReceiverOptions.create<String, ByteArray>(consumerOptions())
            .subscription(setOf(KafkaTopic.NOTIFICATION))
        return KafkaReceiver.create(options)
    }

    private fun consumerOptions(): MutableMap<String, Any> {
        val options = mutableMapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java.name,
            ConsumerConfig.GROUP_ID_CONFIG to "$TEST_GROUP_ID-${UUID.randomUUID()}",
        )
        val buildProperties = kafkaProperties.consumer.buildProperties(null)
        buildProperties.putAll(options)
        return buildProperties
    }

    companion object {
        const val TEST_GROUP_ID = "test-group"
    }
}
