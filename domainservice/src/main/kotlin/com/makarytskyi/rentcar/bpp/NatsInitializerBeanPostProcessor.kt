package com.makarytskyi.rentcar.bpp

import com.google.protobuf.GeneratedMessage
import com.makarytskyi.rentcar.controller.nats.NatsController
import io.nats.client.Dispatcher
import io.nats.client.MessageHandler
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class NatsInitializerBeanPostProcessor(private val dispatcher: Dispatcher) : BeanPostProcessor {

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean is NatsController<*, *>) processBean(bean)
        return bean
    }

    private fun <RequestT : GeneratedMessage, ResponseT : GeneratedMessage> processBean(
        bean: NatsController<RequestT, ResponseT>
    ) {
        val handler = MessageHandler { message ->
            bean.parser.toMono()
                .map { it.parseFrom(message.data) }
                .flatMap { bean.handle(it) }
                .onErrorResume { onParsingError(it, bean.defaultResponse) }
                .subscribe {
                    bean.connection.publish(message.replyTo, it.toByteArray())
                }
        }
        dispatcher.subscribe(bean.subject, bean.queueGroup, handler)
    }

    private fun <ResponseT : GeneratedMessage> onParsingError(
        throwable: Throwable,
        defaultResponse: ResponseT
    ): Mono<ResponseT> {
        val failureDescriptor = defaultResponse.descriptorForType.findFieldByName(FAILURE)
        val messageDescriptor = failureDescriptor.messageType.findFieldByName(MESSAGE_FIELD)
        val response = defaultResponse.defaultInstanceForType.toBuilder().run {
            val failure = newBuilderForField(failureDescriptor)
                .setField(messageDescriptor, throwable.message.orEmpty())
                .build()
            setField(failureDescriptor, failure)
        }.build()
        return (response as ResponseT).toMono()
    }

    companion object {
        private const val FAILURE = "failure"
        private const val MESSAGE_FIELD = "message"
    }
}
