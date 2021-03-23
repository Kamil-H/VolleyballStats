package com.kamilh.utils

import com.kamilh.extensions.toIsoString
import com.kamilh.extensions.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {

    override fun deserialize(decoder: Decoder): LocalDateTime =
        decoder.decodeString().removeTimezoneInfo().toLocalDateTime()!!

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toIsoString())
    }

    private fun String.removeTimezoneInfo(): String =
        this.replace("Z", "")
}