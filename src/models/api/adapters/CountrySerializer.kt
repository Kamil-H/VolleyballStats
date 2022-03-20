package com.kamilh.models.api.adapters

import com.kamilh.models.Country
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object CountrySerializer : KSerializer<Country> {

    override fun deserialize(decoder: Decoder): Country = Country(decoder.decodeString())

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Country", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Country) {
        encoder.encodeString(value.code)
    }
}