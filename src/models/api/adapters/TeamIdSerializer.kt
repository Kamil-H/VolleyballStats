package com.kamilh.models.api.adapters

import com.kamilh.models.TeamId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TeamIdSerializer : KSerializer<TeamId> {

    override fun deserialize(decoder: Decoder): TeamId = TeamId(decoder.decodeLong())

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("TeamId", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: TeamId) {
        encoder.encodeLong(value.value)
    }
}