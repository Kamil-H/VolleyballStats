package com.kamilh.volleyballstats.models.api.adapters

import com.kamilh.volleyballstats.models.Url
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object UrlSerializer : KSerializer<Url> {

    override fun deserialize(decoder: Decoder): Url = Url.create(decoder.decodeString())

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Url", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Url) {
        encoder.encodeString(value.value)
    }
}