@file:UseSerializers(CountrySerializer::class)

package com.kamilh.models.api.league

import com.kamilh.models.Country
import com.kamilh.models.api.adapters.CountrySerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class LeagueResponse(
    val country: Country,
    val division: Int,
)