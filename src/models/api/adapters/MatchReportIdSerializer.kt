package com.kamilh.models.api.adapters

import com.kamilh.models.MatchReportId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MatchReportIdSerializer : KSerializer<MatchReportId> {

    override fun deserialize(decoder: Decoder): MatchReportId = MatchReportId(decoder.decodeLong())

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("MatchReportId", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: MatchReportId) {
        encoder.encodeLong(value.value)
    }
}