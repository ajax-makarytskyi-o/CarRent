package com.makarytskyi.rentcar.repository.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.patch.MongoCarPatch
import com.makarytskyi.rentcar.repository.CarRepository
import io.lettuce.core.RedisException
import java.net.SocketException
import java.time.Duration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.dao.QueryTimeoutException
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.util.retry.Retry

@Repository
@Primary
internal class RedisCarRepository(
    @Qualifier("mongoCarRepository")
    private val mongoCarRepository: CarRepository,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, ByteArray>,
    private val mapper: ObjectMapper,
    @Value("\${redis.key-ttl}")
    private val ttlSeconds: Long,
    @Value("\${redis.retries}")
    private val retries: Long,
    @Value("\${redis.retry-timeout}")
    private val retryTimeout: Long,
) : CarRepository by mongoCarRepository {

    override fun findById(id: String): Mono<MongoCar> =
        reactiveRedisTemplate.opsForValue().get(idRedisKey(id))
            .map { mapper.readValue(it, MongoCar::class.java) }
            .onErrorResume { Mono.empty() }
            .switchIfEmpty {
                mongoCarRepository.findById(id)
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnNext {
                        setRedisKey(idRedisKey(id), mapper.writeValueAsBytes(it), ttlSeconds)
                    }
                    .switchIfEmpty {
                        setRedisKey(idRedisKey(id), byteArrayOf(), ttlSeconds)
                        Mono.empty()
                    }
            }

    override fun create(mongoCar: MongoCar): Mono<MongoCar> =
        mongoCarRepository.create(mongoCar)
            .doOnNext { car ->
                setRedisKey(idRedisKey(car.id.toString()), mapper.writeValueAsBytes(car), ttlSeconds)
                car.plate?.let { plate -> setRedisKey(plateRedisKey(plate), mapper.writeValueAsBytes(car), ttlSeconds) }
            }

    override fun deleteById(id: String): Mono<Unit> =
        mongoCarRepository.deleteById(id)
            .doOnSuccess {
                reactiveRedisTemplate.unlink(idRedisKey(id))
                    .subscribeOn(Schedulers.boundedElastic())
                    .retryWhen(retryOnRedisError())
                    .subscribe()
            }

    override fun patch(id: String, carPatch: MongoCarPatch): Mono<MongoCar> =
        mongoCarRepository.patch(id, carPatch)
            .doOnNext { car ->
                setRedisKey(idRedisKey(car.id.toString()), mapper.writeValueAsBytes(car), ttlSeconds)
                car.plate?.let { plate -> setRedisKey(plateRedisKey(plate), mapper.writeValueAsBytes(car), ttlSeconds) }
            }


    override fun findByPlate(plate: String): Mono<MongoCar> =
        reactiveRedisTemplate.opsForValue().get(plateRedisKey(plate))
            .map { mapper.readValue(it, MongoCar::class.java) }
            .onErrorResume { Mono.empty() }
            .switchIfEmpty {
                mongoCarRepository.findByPlate(plate)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnNext {
                        setRedisKey(plateRedisKey(plate), mapper.writeValueAsBytes(it), ttlSeconds)
                    }
                    .switchIfEmpty {
                        setRedisKey(plateRedisKey(plate), byteArrayOf(), ttlSeconds)
                        Mono.empty()
                    }
            }

    private fun retryOnRedisError() =
        Retry.backoff(retries, Duration.ofSeconds(retryTimeout))
            .filter { throwable -> throwable::class in redisErrorSet }

    private fun setRedisKey(key: String, value: ByteArray, ttlSeconds: Long) =
        reactiveRedisTemplate.opsForValue()
            .set(key, value, Duration.ofSeconds(ttlSeconds))
            .subscribeOn(Schedulers.boundedElastic())
            .retryWhen(retryOnRedisError())
            .onErrorResume { Mono.empty() }
            .subscribe()

    companion object {
        private const val KEY_CAR_PREFIX = "car-redis-prefix"
        fun idRedisKey(id: String): String = "$KEY_CAR_PREFIX-$id"
        fun plateRedisKey(plate: String): String = "$KEY_CAR_PREFIX-$plate"

        val redisErrorSet = hashSetOf(
            SocketException::class,
            RedisException::class,
            RedisConnectionFailureException::class,
            QueryTimeoutException::class,
        )
    }
}
