package com.makarytskyi.rentcar.bpp

import com.makarytskyi.rentcar.annotation.InvocationTracker
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component

@Component
internal class InvocationTrackerBeanPostProcessor : BeanPostProcessor {

    private val classes: MutableMap<String, KClass<*>> = HashMap()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        val beanClass = bean::class
        if (beanClass.findAnnotation<InvocationTracker>() != null) {
            classes[beanName] = bean::class
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        val simpleClassName = classes[beanName]?.simpleName
        return classes[beanName]?.let { beanClass ->
            Proxy.newProxyInstance(
                beanClass.java.classLoader,
                beanClass.java.interfaces,
                InvocationTrackerHandler(bean, simpleClassName)
            )
        } ?: bean
    }

    private class InvocationTrackerHandler(private val bean: Any, private val simpleClassName: String?) :
        InvocationHandler {

        @Suppress("SwallowedException", "SpreadOperator")
        override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
            val startTime = System.currentTimeMillis()
            return try {
                val result = method?.invoke(bean, *(args ?: emptyArray()))
                val endTime = System.currentTimeMillis()
                log.atInfo()
                    .setMessage("Method '{}' of class '{}' returned {} executed in {} ms with arguments: {} ")
                    .addArgument(method?.name)
                    .addArgument(simpleClassName)
                    .addArgument(result)
                    .addArgument(endTime - startTime)
                    .addArgument { args?.joinToString(", ", "[ ", " ]") }
                    .log()
                result
            } catch (e: InvocationTargetException) {
                val endTime = System.currentTimeMillis()

                log.atError()
                    .setMessage("Method '{}' of class '{}' with arguments: {} finished with exception in {} ms")
                    .addArgument(method?.name)
                    .addArgument(simpleClassName)
                    .addArgument { args?.joinToString(", ", "[ ", " ]") }
                    .addArgument(endTime - startTime)
                    .setCause(e.targetException)
                    .log()

                throw e.targetException
            }
        }

        companion object {
            private val log: Logger = LoggerFactory.getLogger(InvocationTrackerBeanPostProcessor::class.java)
        }
    }
}
