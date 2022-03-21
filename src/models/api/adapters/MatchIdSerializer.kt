package com.kamilh.models.api.adapters

import com.kamilh.models.MatchId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MatchIdSerializer : KSerializer<MatchId> {

    override fun deserialize(decoder: Decoder): MatchId = MatchId(decoder.decodeLong())

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("MatchId", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: MatchId) {
        encoder.encodeLong(value.value)
    }
}