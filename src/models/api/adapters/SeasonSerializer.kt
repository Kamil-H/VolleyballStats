package com.kamilh.models.api.adapters

import com.kamilh.models.Season
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SeasonSerializer : KSerializer<Season> {

    override fun deserialize(decoder: Decoder): Season = Season.create(decoder.decodeInt())

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Season", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Season) {
        encoder.encodeInt(value.value)
    }
}