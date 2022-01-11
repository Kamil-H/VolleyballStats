package com.kamilh.models

import models.PlayerWithDetails
import java.time.LocalDate
import java.time.LocalDateTime

fun playerOf(
    id: PlayerId = playerIdOf(),
    name: String = "",
    imageUrl: Url? = null,
    team: TeamId = teamIdOf(),
    specialization: Player.Specialization = Player.Specialization.Libero,
    updatedAt: LocalDateTime = LocalDateTime.now(),
): Player = Player(
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
    player: Player = playerOf(),
    details: PlayerDetails = playerDetailsOf(),
): PlayerWithDetails = PlayerWithDetails(
    player = player,
    details = details,
)