// Workaround the problem with Unresolved reference
package com.kamilh.volleyballstats.domain.player

import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.playerIdOf
import com.kamilh.volleyballstats.domain.teamIdOf
import com.kamilh.volleyballstats.utils.localDate
import com.kamilh.volleyballstats.utils.localDateTime

fun playerOf(
    id: PlayerId = playerIdOf(),
    name: String = "",
    imageUrl: Url? = null,
    team: TeamId = teamIdOf(),
    specialization: Specialization = Specialization.Libero,
    date: LocalDate = localDate(),
    height: Int? = null,
    weight: Int? = null,
    range: Int? = null,
    number: Int = 0,
    updatedAt: LocalDateTime = localDateTime(),
): Player = Player(
    id = id,
    name = name,
    imageUrl = imageUrl,
    team = team,
    specialization = specialization,
    date = date,
    height = height,
    weight = weight,
    range = range,
    number = number,
    updatedAt = updatedAt,
)