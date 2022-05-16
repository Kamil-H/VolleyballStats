package com.kamilh.volleyballstats.models.api.adapters

import com.kamilh.volleyballstats.models.TourId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TourIdSerializer : KSerializer<TourId> {

    override fun deserialize(decoder: Decoder): TourId = TourId(decoder.decodeLong())

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("TourId", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: TourId) {
        encoder.encodeLong(value.value)
    }
}