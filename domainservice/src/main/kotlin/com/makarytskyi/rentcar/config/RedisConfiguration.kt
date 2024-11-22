package com.makarytskyi.rentcar.config

import io.lettuce.core.ClientOptions
import io.lettuce.core.TimeoutOptions
import java.time.Duration
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableCaching
class RedisConfiguration {

    @Value("\${redis.host}")
    private lateinit var host: String

    @Value("\${redis.port}")
    private lateinit var port: String

    @Value("\${redis.timeout}")
    private lateinit var timeout: String

    @Bean
    fun reactiveRedisTemplate(connectionFactory: ReactiveRedisConnectionFactory):
            ReactiveRedisTemplate<String, ByteArray> {
        val context = RedisSerializationContext
            .newSerializationContext<String, ByteArray>(StringRedisSerializer())
            .value(RedisSerializer.byteArray())
            .build()

        return ReactiveRedisTemplate(connectionFactory, context)
    }

    @Bean
    @Primary
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        val config = RedisStandaloneConfiguration(host, port.toInt())
        val options =
            ClientOptions.builder().timeoutOptions(TimeoutOptions.enabled(Duration.ofMillis(timeout.toLong()))).build()
        return LettuceConnectionFactory(config, LettuceClientConfiguration.builder().clientOptions(options).build())
    }
}
