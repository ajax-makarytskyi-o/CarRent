package com.makarytskyi.rentcar.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.kafka.handler.notifier.KafkaGlobalExceptionHandler

@Configuration
class KafkaConfiguration {

    @Bean
    fun kafkaGlobalExceptionHandler(): KafkaGlobalExceptionHandler {
        return object : KafkaGlobalExceptionHandler {
            override fun doOnError(kafkaEvent: KafkaEvent<*>, ex: Throwable): Mono<Unit> {
                log.atError()
                    .setMessage("Error processing Kafka event: {}, Exception: {}")
                    .addArgument(kafkaEvent)
                    .addArgument(ex.message)
                    .setCause(ex)
                    .log()

                return Unit.toMono()
            }
        }
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(KafkaConfiguration::class.java)
    }
}
