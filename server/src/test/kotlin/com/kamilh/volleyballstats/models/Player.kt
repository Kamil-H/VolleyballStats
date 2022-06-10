package com.kamilh.volleyballstats.domain

import com.kamilh.volleyballstats.datetime.LocalDate
import com.kamilh.volleyballstats.datetime.LocalDateTime
import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.Url
import com.kamilh.volleyballstats.models.PlayerDetails
import com.kamilh.volleyballstats.models.PlayerSnapshot
import com.kamilh.volleyballstats.models.PlayerWithDetails
import com.kamilh.volleyballstats.models.TeamPlayer
import com.kamilh.volleyballstats.utils.localDate
import com.kamilh.volleyballstats.utils.localDateTime

fun playerSnapshotOf(
    id: PlayerId = playerIdOf(),
    name: String = "",
): PlayerSnapshot = PlayerSnapshot(
    id = id,
    name = name,
)

fun teamPlayerOf(
    id: PlayerId = playerIdOf(),
    name: String = "",
    imageUrl: Url? = null,
    team: TeamId = teamIdOf(),
    specialization: Specialization = Specialization.Libero,
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