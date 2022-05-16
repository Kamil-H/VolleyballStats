@file:UseSerializers(CountrySerializer::class)

package com.kamilh.volleyballstats.models.api.league

import com.kamilh.volleyballstats.models.Country
import com.kamilh.volleyballstats.models.api.adapters.CountrySerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class LeagueResponse(
    val country: Country,
    val division: Int,
)