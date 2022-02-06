package com.kamilh.models

import java.time.OffsetDateTime

sealed class AllMatchesItem {

    abstract val id: MatchId
    abstract val date: OffsetDateTime?
    abstract val home: TeamId
    abstract val away: TeamId

    data class PotentiallyFinished(
        override val id: MatchId,
        override val date: OffsetDateTime?,
        override val home: TeamId,
        override val away: TeamId,
    ) : AllMatchesItem()

    data class Scheduled(
        override val id: MatchId,
        override val date: OffsetDateTime,
        override val home: TeamId,
        override val away: TeamId,
    ) : AllMatchesItem()

    data class NotScheduled(
        override val id: MatchId,
        override val date: OffsetDateTime?,
        override val home: TeamId,
        override val away: TeamId,
    ) : AllMatchesItem()

    data class Saved(
        override val id: MatchId,
        override val date: OffsetDateTime?,
        override val home: TeamId,
        override val away: TeamId,
        val matchReportId: MatchReportId,
        val winnerId: TeamId,
        val endTime: OffsetDateTime,
    ) : AllMatchesItem()
}