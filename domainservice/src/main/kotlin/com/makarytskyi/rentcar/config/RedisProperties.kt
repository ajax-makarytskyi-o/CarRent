package com.makarytskyi.rentcar.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RedisProperties(
    @Value("\${redis.port}")
    val port: Int,
    @Value("\${redis.timeout}")
    val timeout: Int,
    @Value("\${redis.host}")
    val host: String,
    @Value("\${redis.key-ttl}")
    val ttlSeconds: Long,
    @Value("\${redis.retries}")
    val retries: Long,
    @Value("\${redis.retry-timeout}")
    val retryTimeout: Long,
)
