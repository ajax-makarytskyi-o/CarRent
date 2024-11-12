package com.makarytskyi.rentcar.kafka

import com.makarytskyi.internalapi.subject.KafkaTopic
import java.util.UUID
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions

@Configuration
class TestConfiguration(
    private val kafkaProperties: KafkaProperties,
) {
    @Bean("notificationReceiver")
    @Scope("prototype")
    fun notificationReceiver(): KafkaReceiver<String, ByteArray> {
        return createReceiver(consumerOptions(), "$TEST_GROUP_ID-${UUID.randomUUID()}", KafkaTopic.NOTIFICATION)
    }

    private fun createReceiver(
        receiverOptions: MutableMap<String, Any>,
        groupId: String,
        topic: String
    ): KafkaReceiver<String, ByteArray> {
        receiverOptions[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        val options = ReceiverOptions.create<String, ByteArray>(receiverOptions)
            .subscription(setOf(topic))
        return KafkaReceiver.create(options)
    }

    private fun consumerOptions(): MutableMap<String, Any> {
        val options = mutableMapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java.name,
        )
        val buildProperties = kafkaProperties.consumer.buildProperties(null)
        buildProperties.putAll(options)
        return buildProperties
    }

    companion object {
        const val TEST_GROUP_ID = "test-group"
    }
}
