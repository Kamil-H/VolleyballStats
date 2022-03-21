package com.kamilh.models

import com.kamilh.datetime.ZonedDateTime

sealed class Match {

    abstract val id: MatchId
    abstract val date: ZonedDateTime?
    abstract val home: TeamId
    abstract val away: TeamId

    data class PotentiallyFinished(
        override val id: MatchId,
        override val date: ZonedDateTime,
        override val home: TeamId,
        override val away: TeamId,
    ) : Match()

    data class Scheduled(
        override val id: MatchId,
        override val date: ZonedDateTime,
        override val home: TeamId,
        override val away: TeamId,
    ) : Match()

    data class NotScheduled(
        override val id: MatchId,
        override val date: ZonedDateTime?,
        override val home: TeamId,
        override val away: TeamId,
    ) : Match()

    data class Finished(
        override val id: MatchId,
        override val date: ZonedDateTime,
        override val home: TeamId,
        override val away: TeamId,
        val winnerId: TeamId,
        val endTime: ZonedDateTime,
    ) : Match()
}