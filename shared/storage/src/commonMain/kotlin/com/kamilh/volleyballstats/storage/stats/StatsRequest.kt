package com.kamilh.volleyballstats.storage.stats

import com.kamilh.volleyballstats.domain.models.TourId

interface StatsRequest {

    val groupBy: GroupBy

    val tourId: TourId

    enum class GroupBy {
        Player, Team
    }
}

internal val StatsRequest.GroupBy.fieldName: String
    get() = when (this) {
        StatsRequest.GroupBy.Player -> "player"
        StatsRequest.GroupBy.Team -> "team"
    }
