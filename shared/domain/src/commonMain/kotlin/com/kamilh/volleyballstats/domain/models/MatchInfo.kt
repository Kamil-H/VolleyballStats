package com.kamilh.volleyballstats.domain.models

import com.kamilh.volleyballstats.datetime.ZonedDateTime

sealed class MatchInfo {

    abstract val id: MatchId
    abstract val date: ZonedDateTime?
    abstract val home: TeamId
    abstract val away: TeamId

    data class PotentiallyFinished(
        override val id: MatchId,
        override val date: ZonedDateTime,
        override val home: TeamId,
        override val away: TeamId,
    ) : MatchInfo()

    data class Scheduled(
        override val id: MatchId,
        override val date: ZonedDateTime,
        override val home: TeamId,
        override val away: TeamId,
    ) : MatchInfo()

    data class NotScheduled(
        override val id: MatchId,
        override val date: ZonedDateTime?,
        override val home: TeamId,
        override val away: TeamId,
    ) : MatchInfo()

    data class Finished(
        override val id: MatchId,
        override val date: ZonedDateTime,
        override val home: TeamId,
        override val away: TeamId,
        val winnerId: TeamId,
        val endTime: ZonedDateTime,
    ) : MatchInfo()
}
