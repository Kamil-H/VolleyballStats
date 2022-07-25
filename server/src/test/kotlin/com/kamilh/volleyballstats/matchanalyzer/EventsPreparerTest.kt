package com.kamilh.volleyballstats.matchanalyzer

import com.kamilh.volleyballstats.models.*
import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class EventsPreparerTest {

    private val preparer = EventsPreparerImpl()

    @Test
    fun `test if Libero Events don't get filtered out when it happens between Rally and VideoChallenge's NoChange`() {
        // GIVEN
        val rally = rallyOf()
        val libero = liberoOf()
        val videoChallenge = videoChallengeOf(scoreChange = Event.VideoChallenge.ScoreChange.NoChange)
        val events = listOf(rally, libero, videoChallenge)

        // WHEN
        val prepared = preparer.prepare(events)

        // THEN
        assert(prepared.contains(rally))
        assert(prepared.contains(libero))
        assert(prepared.contains(videoChallenge))
    }

    @ParameterizedTest
    @EnumSource(value = Event.VideoChallenge.ScoreChange::class, names = ["RepeatLast", "AssignToOther"])
    fun `test if Libero Events gets filtered out when it happens between Rally and VideoChallenge's RepeatLast and AssignToOther`(scoreChange: Event.VideoChallenge.ScoreChange) {
        // GIVEN
        val rally = rallyOf()
        val libero = liberoOf()
        val videoChallenge = videoChallengeOf(scoreChange = scoreChange)
        val events = listOf(rally, libero, videoChallenge)

        // WHEN
        val prepared = preparer.prepare(events)

        // THEN
        assert(prepared.contains(rally))
        assert(!prepared.contains(libero))
        assert(prepared.contains(videoChallenge))
    }

    @ParameterizedTest
    @EnumSource(value = Event.VideoChallenge.ScoreChange::class, names = ["RepeatLast", "AssignToOther"])
    fun `test if all Libero Events gets filtered out when it happens between Rally and VideoChallenge's RepeatLast and AssignToOther`(scoreChange: Event.VideoChallenge.ScoreChange) {
        // GIVEN
        val rally = rallyOf()
        val libero = liberoOf(team = TeamType.Home)
        val libero1 = liberoOf(team = TeamType.Away)
        val videoChallenge = videoChallengeOf(scoreChange = scoreChange)
        val events = listOf(rally, libero, libero1, videoChallenge)

        // WHEN
        val prepared = preparer.prepare(events)

        // THEN
        assert(prepared.contains(rally))
        assert(!prepared.contains(libero))
        assert(!prepared.contains(libero1))
        assert(prepared.contains(videoChallenge))
    }
}