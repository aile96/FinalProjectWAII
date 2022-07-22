package it.polito.s280048.ticketcatalogue.config.consumer

import it.polito.s280048.ticketcatalogue.dtos.PaymentInfoDTOQueue
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.LoggerFactory
import kotlin.text.Charsets.UTF_8


class ProductDeserializer : Deserializer<PaymentInfoDTOQueue> {
    private val objectMapper = ObjectMapper()
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(topic: String?, data: ByteArray?): PaymentInfoDTOQueue? {
        log.info("Deserializing...")
        return objectMapper.readValue(
            String(
                data ?: throw SerializationException("Error when deserializing byte[] to Product"), UTF_8
            ), PaymentInfoDTOQueue::class.java
        )
    }

    override fun close() {}

}
