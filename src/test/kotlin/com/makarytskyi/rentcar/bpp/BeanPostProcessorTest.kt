package com.makarytskyi.rentcar.bpp

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import com.makarytskyi.rentcar.exception.NotFoundException
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.service.CarService
import com.makarytskyi.rentcar.service.impl.CarServiceImpl
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import java.lang.reflect.Proxy
import kotlin.test.Test
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@ExtendWith(MockKExtension::class)
internal class BeanPostProcessorTest {
    @MockK
    lateinit var repository: CarRepository

    @MockK(relaxed = true)
    lateinit var mockAppender: Appender<ILoggingEvent>

    @InjectMockKs
    lateinit var service: CarServiceImpl

    val beanName = "service"
    val postProcessor: InvocationTrackerBeanPostProcessor = InvocationTrackerBeanPostProcessor()
    val loggerFactory: ILoggerFactory = LoggerFactory.getILoggerFactory()
    val rootLogger = loggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger

    @BeforeEach
    fun before() {
        rootLogger.addAppender(mockAppender)
    }

    @Test
    fun `beanPostProcessor should return proxy`() {
        // GIVEN
        postProcessor.postProcessBeforeInitialization(service, beanName)

        // WHEN
        val proxyService = postProcessor.postProcessAfterInitialization(service, beanName)

        // THEN
        assertTrue(
            Proxy.isProxyClass(proxyService::class.java),
            "Class returned by post processor should be proxy class."
        )
    }

    @Test
    fun `proxied service should write in logger`() {
        // GIVEN
        postProcessor.postProcessBeforeInitialization(service, beanName)
        every { repository.findAll(0, 10) }.returns(Flux.empty())

        // WHEN
        val proxyService = postProcessor.postProcessAfterInitialization(service, beanName) as CarService
        proxyService.findAll(0, 10).blockFirst()

        // THEN
        verify(exactly = 1) { mockAppender.doAppend(any()) }
    }

    @Test
    fun `original object should not use logger`() {
        // GIVEN
        val service = CarServiceImpl(repository)
        every { repository.findAll(0, 10) }.returns(Flux.empty())

        // WHEN
        service.findAll(0, 10)

        // THEN
        verify(exactly = 0) { mockAppender.doAppend(any()) }
    }

    @Test
    fun `exception in proxied service should write error in logger`() {
        // GIVEN
        val id = "unexistingId"
        every { repository.findById(id) }.returns(Mono.empty())

        // WHEN
        postProcessor.postProcessBeforeInitialization(service, beanName)
        val proxyService = postProcessor.postProcessAfterInitialization(service, beanName) as CarService

        // THEN
        assertThrows<NotFoundException> { proxyService.getById(id).block() }
        verify(exactly = 1) { mockAppender.doAppend(any()) }
    }
}
