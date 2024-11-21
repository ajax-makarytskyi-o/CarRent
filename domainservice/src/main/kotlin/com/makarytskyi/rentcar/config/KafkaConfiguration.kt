package com.makarytskyi.rentcar.config

import com.makarytskyi.internalapi.topic.KafkaTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaConfiguration(
    private val kafkaProperties: KafkaProperties
) {

    @Bean
    fun kafkaSender(): KafkaSender<String, ByteArray> {
        return createProducer(producerOptions())
    }

    @Bean
    fun createRepairingKafkaReceiver(): KafkaReceiver<String, ByteArray> {
        return createReceiver(consumerOptions(), GROUP_ID_REPAIRING, KafkaTopic.Repairing.REPAIRING_CREATE)
    }

    @Bean
    fun createOrderKafkaReceiver(): KafkaReceiver<String, ByteArray> {
        return createReceiver(consumerOptions(), GROUP_ID_ORDER, KafkaTopic.Order.ORDER_CREATE)
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

    private fun createProducer(
        producerOptions: Map<String, Any>
    ): KafkaSender<String, ByteArray> {
        return KafkaSender.create(
            SenderOptions.create(producerOptions)
        )
    }

    private fun producerOptions(): Map<String, Any> {
        val options = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java,
        )
        val buildProperties = kafkaProperties.producer.buildProperties(null)
        buildProperties.putAll(options)
        return buildProperties
    }

    private fun consumerOptions(): MutableMap<String, Any> {
        val options = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java,
        )
        val buildProperties = kafkaProperties.consumer.buildProperties(null)
        buildProperties.putAll(options)
        return buildProperties
    }

    companion object {
        const val GROUP_ID_REPAIRING = "group-rentcar-repairing"
        const val GROUP_ID_ORDER = "group-rentcar-order"
    }
}
