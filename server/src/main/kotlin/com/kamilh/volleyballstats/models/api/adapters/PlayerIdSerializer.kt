package com.kamilh.volleyballstats.models.api.adapters

import com.kamilh.volleyballstats.models.PlayerId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object PlayerIdSerializer : KSerializer<PlayerId> {

    override fun deserialize(decoder: Decoder): PlayerId = PlayerId(decoder.decodeLong())

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("PlayerId", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: PlayerId) {
        encoder.encodeLong(value.value)
    }
}