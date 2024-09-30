package com.makarytskyi.rentcar.bpp

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import com.makarytskyi.rentcar.exception.NotFoundException
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.service.CarService
import com.makarytskyi.rentcar.service.impl.CarServiceImpl
import java.lang.reflect.Proxy
import kotlin.test.Test
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ExtendWith(MockitoExtension::class)
internal class BeanPostProcessorTests {

    @Mock
    lateinit var repository: CarRepository

    @Mock
    lateinit var mockAppender: Appender<ILoggingEvent>

    @InjectMocks
    lateinit var service: CarServiceImpl

    val beanName = "service"
    val postProcessor: InvocationTrackerBeanPostProcessor = InvocationTrackerBeanPostProcessor()
    val loggerFactory = LoggerFactory.getILoggerFactory()
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
        assertTrue(Proxy.isProxyClass(proxyService::class.java))
    }

    @Test
    fun `proxied service should write in logger`() {
        postProcessor.postProcessBeforeInitialization(service, beanName)

        // WHEN
        val proxyService = postProcessor.postProcessAfterInitialization(service, beanName) as CarService
        proxyService.findAll()

        // THEN
        Mockito.verify(mockAppender, Mockito.times(1)).doAppend(ArgumentMatchers.any(ILoggingEvent::class.java))
    }

    @Test
    fun `original object should not use logger`() {
        // GIVEN
        val service = CarServiceImpl(repository)

        // WHEN
        service.findAll()

        // THEN
        Mockito.verify(mockAppender, Mockito.times(0)).doAppend(ArgumentMatchers.any())
    }

    @Test
    fun `exception in proxied service should write error in logger`() {
        // GIVEN
        val id = "unexistingId"

        // WHEN
        postProcessor.postProcessBeforeInitialization(service, beanName)
        val proxyService = postProcessor.postProcessAfterInitialization(service, beanName) as CarService

        // THEN
        assertThrows<NotFoundException> { proxyService.getById(id) }
        Mockito.verify(mockAppender, Mockito.times(1)).doAppend(ArgumentMatchers.any(ILoggingEvent::class.java))
    }
}
