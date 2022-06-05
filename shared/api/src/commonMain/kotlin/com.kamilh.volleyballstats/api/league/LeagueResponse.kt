@file:UseSerializers(CountrySerializer::class)

package com.kamilh.volleyballstats.api.league

import com.kamilh.volleyballstats.api.adapters.CountrySerializer
import com.kamilh.volleyballstats.domain.models.Country
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class LeagueResponse(
    val country: Country,
    val division: Int,
)