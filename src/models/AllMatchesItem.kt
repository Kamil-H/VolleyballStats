package com.kamilh.models

import java.time.LocalDateTime

sealed class AllMatchesItem {
    abstract val id: MatchId

    data class PotentiallyFinished(override val id: MatchId): AllMatchesItem()
    data class Scheduled(override val id: MatchId, val date: LocalDateTime): AllMatchesItem()
    data class NotScheduled(override val id: MatchId): AllMatchesItem()
}