package com.kamilh.volleyballstats.storage.stats

import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId

sealed interface StatsRequest {

    val groupBy: GroupBy

    val seasons: List<Season>

    val specializations: List<Specialization>

    val teams: List<TeamId>

    val minAttempts: Long

    enum class GroupBy {
        Player, Team
    }
}

internal val StatsRequest.GroupBy.fieldName: String
    get() = when (this) {
        StatsRequest.GroupBy.Player -> "player"
        StatsRequest.GroupBy.Team -> "team"
    }
