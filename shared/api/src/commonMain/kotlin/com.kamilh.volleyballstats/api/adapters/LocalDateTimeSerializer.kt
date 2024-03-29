package com.kamilh.volleyballstats.api.adapters

import com.kamilh.volleyballstats.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {

    override fun deserialize(decoder: Decoder): LocalDateTime =
        LocalDateTime.parse(decoder.decodeString().removeTimezoneInfo())

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toIso8601String())
    }

    private fun String.removeTimezoneInfo(): String =
        this.replace("Z", "")
}