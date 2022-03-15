package com.kamilh.models

import com.kamilh.datetime.ZonedDateTime

sealed class AllMatchesItem {

    abstract val id: MatchId
    abstract val date: ZonedDateTime?
    abstract val home: TeamId
    abstract val away: TeamId

    data class PotentiallyFinished(
        override val id: MatchId,
        override val date: ZonedDateTime?,
        override val home: TeamId,
        override val away: TeamId,
    ) : AllMatchesItem()

    data class Scheduled(
        override val id: MatchId,
        override val date: ZonedDateTime,
        override val home: TeamId,
        override val away: TeamId,
    ) : AllMatchesItem()

    data class NotScheduled(
        override val id: MatchId,
        override val date: ZonedDateTime?,
        override val home: TeamId,
        override val away: TeamId,
    ) : AllMatchesItem()

    data class Saved(
        override val id: MatchId,
        override val date: ZonedDateTime?,
        override val home: TeamId,
        override val away: TeamId,
        val matchReportId: MatchReportId,
        val winnerId: TeamId,
        val endTime: ZonedDateTime,
    ) : AllMatchesItem()
}