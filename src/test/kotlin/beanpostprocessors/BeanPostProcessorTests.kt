package beanpostprocessors

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import com.makarytskyi.rentcar.bpp.InvocationTrackerBeanPostProcessor
import com.makarytskyi.rentcar.exception.ResourceNotFoundException
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.service.CarService
import com.makarytskyi.rentcar.service.impl.CarServiceImpl
import kotlin.test.Test
import kotlin.test.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
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

    val postProcessor: InvocationTrackerBeanPostProcessor = InvocationTrackerBeanPostProcessor()
    val loggerFactory = LoggerFactory.getILoggerFactory()
    val rootLogger = loggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger

    @BeforeEach
    fun before() {
        rootLogger.addAppender(mockAppender)
    }

    @Test
    fun `beanPostProcessor should return proxy`() {
        //GIVEN
        val name = "service"
        postProcessor.postProcessBeforeInitialization(service, name)

        //WHEN
        val proxyService = postProcessor.postProcessAfterInitialization(service, name)

        //THEN
        assertNotNull(proxyService)
        assertNotEquals(proxyService!!::class.java, service::class.java)
    }

    @Test
    fun `proxied controller should write in logger`() {
        val name = "service"
        postProcessor.postProcessBeforeInitialization(service, name)

        //WHEN
        val proxyService = postProcessor.postProcessAfterInitialization(service, name) as CarService
        proxyService.findAll()
        proxyService.findAll()

        //THEN
        assertNotEquals(proxyService::class.java, service::class.java)
        verify(mockAppender, times(2)).doAppend(any(ILoggingEvent::class.java))
    }

    @Test
    fun `original object should not use logger`() {
        //GIVEN
        val service = CarServiceImpl(repository)

        //WHEN
        service.findAll()
        service.findAll()

        //THEN
        verify(mockAppender, times(0)).doAppend(any())
    }

    @Test
    fun `exception in proxied controller should write error in logger`() {
        //GIVEN
        val id = "unexistingId"
        val name = "service"

        //WHEN
        postProcessor.postProcessBeforeInitialization(service, name)
        val proxyService = postProcessor.postProcessAfterInitialization(service, name) as CarService

        //THEN
        assertThrows<ResourceNotFoundException> { proxyService.getById(id) }
        assert(proxyService::class != service::class) { "Proxy should be created for the bean" }
        verify(mockAppender, times(1)).doAppend(any(ILoggingEvent::class.java))
    }
}
