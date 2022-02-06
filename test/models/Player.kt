package com.kamilh.models

import models.PlayerWithDetails
import java.time.LocalDate
import java.time.LocalDateTime

fun playerOf(
    id: PlayerId = playerIdOf(),
    name: String = "",
    imageUrl: Url? = null,
    team: TeamId = teamIdOf(),
    specialization: TeamPlayer.Specialization = TeamPlayer.Specialization.Libero,
    updatedAt: LocalDateTime = LocalDateTime.now(),
): TeamPlayer = TeamPlayer(
    id = id,
    name = name,
    imageUrl = imageUrl,
    team = team,
    specialization = specialization,
    updatedAt = updatedAt,
)

fun playerDetailsOf(
    date: LocalDate = LocalDate.now(),
    height: Int? = null,
    weight: Int? = null,
    range: Int? = null,
    number: Int = 0,
    updatedAt: LocalDateTime = LocalDateTime.now(),
): PlayerDetails = PlayerDetails(
    date = date,
    height = height,
    weight = weight,
    range = range,
    number = number,
    updatedAt = updatedAt,
)

fun playerWithDetailsOf(
    teamPlayer: TeamPlayer = playerOf(),
    details: PlayerDetails = playerDetailsOf(),
): PlayerWithDetails = PlayerWithDetails(
    teamPlayer = teamPlayer,
    details = details,
)