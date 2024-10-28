package com.makarytskyi.rentcar.bpp

import com.google.protobuf.GeneratedMessageV3
import com.makarytskyi.rentcar.controller.nats.NatsController
import io.nats.client.Connection
import io.nats.client.Dispatcher
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class NatsInitializerBeanPostProcessor(private val natsConnection: Connection) : BeanPostProcessor {

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean is NatsController<*, *>) processBean(bean)
        return bean
    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> processBean(
        bean: NatsController<RequestT, ResponseT>
    ) {
        val dispatcher: Dispatcher = natsConnection.createDispatcher { message ->
            bean.parser.toMono()
                .map { it.parseFrom(message.data) }
                .flatMap { bean.handle(it) }
                .subscribe {
                    natsConnection.publish(message.replyTo, it.toByteArray())
                }
        }
        dispatcher.subscribe(bean.subject, bean.queueGroup)
    }
}
