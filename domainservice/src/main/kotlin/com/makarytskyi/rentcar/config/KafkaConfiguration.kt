package com.makarytskyi.rentcar.config

import com.makarytskyi.internalapi.subject.KafkaTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaConfiguration(
    @Value("\${kafka.bootstrap-servers}") private val bootstrapServers: String,
    private val kafkaProperties: KafkaProperties
) {

    @Bean
    fun kafkaSender(): KafkaSender<String, ByteArray> = KafkaSender.create(
        SenderOptions.create(producerOptions())
    )

    @Bean
    fun kafkaReceiver(): KafkaReceiver<String, ByteArray> {
        val options = ReceiverOptions.create<String, ByteArray>(consumerOptions())
            .subscription(setOf(KafkaTopic.REPAIRING_CREATE))
        return KafkaReceiver.create(options)
    }

    private fun producerOptions(): MutableMap<String, Any> {
        val options = mutableMapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java.name,
        )
        val buildProperties = kafkaProperties.producer.buildProperties(null)
        buildProperties.putAll(options)
        return buildProperties
    }

    private fun consumerOptions(): MutableMap<String, Any> {
        val options = mutableMapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java.name,
            ConsumerConfig.GROUP_ID_CONFIG to GROUP_ID,
        )
        val buildProperties = kafkaProperties.consumer.buildProperties(null)
        buildProperties.putAll(options)
        return buildProperties
    }

    companion object {
        const val GROUP_ID = "group-rentcar"
    }
}
