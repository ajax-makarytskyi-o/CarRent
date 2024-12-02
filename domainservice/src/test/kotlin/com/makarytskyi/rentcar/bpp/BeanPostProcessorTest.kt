package com.makarytskyi.rentcar.bpp

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.rentcar.car.application.port.input.CarInputPort
import com.makarytskyi.rentcar.car.application.port.output.CarOutputPort
import com.makarytskyi.rentcar.car.application.service.CarService
import com.makarytskyi.rentcar.common.bpp.InvocationTrackerBeanPostProcessor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.spyk
import io.mockk.verify
import java.lang.reflect.Proxy
import kotlin.test.Test
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.ILoggerFactory
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@ExtendWith(MockKExtension::class)
internal class BeanPostProcessorTest {
    @MockK
    lateinit var repository: CarOutputPort

    private val mockAppender: Appender<ILoggingEvent> = spyk()

    @InjectMockKs
    lateinit var service: CarService

    val beanName = "service"
    val postProcessor: InvocationTrackerBeanPostProcessor = InvocationTrackerBeanPostProcessor()
    val loggerFactory: ILoggerFactory = LoggerFactory.getILoggerFactory()
    val rootLogger = loggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger

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
        every { repository.findAll(0, 10) } returns Flux.empty()

        // WHEN
        val proxyService = postProcessor.postProcessAfterInitialization(service, beanName) as CarInputPort
        proxyService.findAll(0, 10).blockFirst()

        // THEN
        verify(exactly = 1) { mockAppender.doAppend(any()) }
    }

    @Test
    fun `original object should not use logger`() {
        // GIVEN
        val service = CarService(repository)
        every { repository.findAll(0, 10) } returns Flux.empty()

        // WHEN
        service.findAll(0, 10)

        // THEN
        verify(exactly = 0) { mockAppender.doAppend(any()) }
    }

    @Test
    fun `exception in proxied service should write error in logger`() {
        // GIVEN
        val id = "unexistingId"
        every { repository.findById(id) } returns Mono.empty()

        // WHEN
        postProcessor.postProcessBeforeInitialization(service, beanName)
        val proxyService = postProcessor.postProcessAfterInitialization(service, beanName) as CarInputPort

        // THEN
        assertThrows<NotFoundException> { proxyService.getById(id).block() }
        verify(exactly = 1) { mockAppender.doAppend(any()) }
    }
}
