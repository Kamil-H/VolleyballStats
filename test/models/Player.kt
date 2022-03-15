package com.kamilh.models

import com.kamilh.datetime.LocalDate
import com.kamilh.datetime.LocalDateTime
import com.kamilh.utils.localDate
import com.kamilh.utils.localDateTime
import models.PlayerWithDetails

fun playerOf(
    id: PlayerId = playerIdOf(),
    name: String = "",
): Player = Player(
    id = id,
    name = name,
)

fun teamPlayerOf(
    id: PlayerId = playerIdOf(),
    name: String = "",
    imageUrl: Url? = null,
    team: TeamId = teamIdOf(),
    specialization: TeamPlayer.Specialization = TeamPlayer.Specialization.Libero,
    updatedAt: LocalDateTime = localDateTime(),
): TeamPlayer = TeamPlayer(
    id = id,
    name = name,
    imageUrl = imageUrl,
    team = team,
    specialization = specialization,
    updatedAt = updatedAt,
)

fun playerDetailsOf(
    date: LocalDate = localDate(),
    height: Int? = null,
    weight: Int? = null,
    range: Int? = null,
    number: Int = 0,
    updatedAt: LocalDateTime = localDateTime(),
): PlayerDetails = PlayerDetails(
    date = date,
    height = height,
    weight = weight,
    range = range,
    number = number,
    updatedAt = updatedAt,
)

fun playerWithDetailsOf(
    teamPlayer: TeamPlayer = teamPlayerOf(),
    details: PlayerDetails = playerDetailsOf(),
): PlayerWithDetails = PlayerWithDetails(
    teamPlayer = teamPlayer,
    details = details,
)