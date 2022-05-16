package com.kamilh.volleyballstats.match_analyzer

import com.kamilh.volleyballstats.match_analyzer.LineupMutator
import com.kamilh.volleyballstats.models.Lineup
import com.kamilh.volleyballstats.models.PlayerPosition
import com.kamilh.volleyballstats.models.playerIdOf
import org.junit.Test

class LineupMutatorTest {

    @Test
    fun `test if rotate works as expected`() {
        // GIVEN
        val playerIds = listOf(0, 1, 2, 3, 4, 5)
        val startingLineup = lineupOf(playerIds)
        val mutator = LineupMutator(startingLineup = startingLineup)

        // WHEN
        mutator.rotate()
        val after = mutator.currentLineup

        // THEN
        val expectedRotation = listOf(1, 2, 3, 4, 5, 0)
        assert(lineupOf(expectedRotation) == after)
    }

    @Test
    fun `test if substitution works as expected`() {
        // GIVEN
        val playerIds = listOf(0, 1, 2, 3, 4, 5)
        val startingLineup = lineupOf(playerIds)
        val mutator = LineupMutator(startingLineup = startingLineup)
        val inPlayerId = playerIdOf(6)
        val outPlayerId = playerIdOf(2)

        // WHEN
        mutator.substitution(`in` = inPlayerId, out = outPlayerId)
        val after = mutator.currentLineup

        // THEN
        assert(inPlayerId == after.p3)
    }

    @Test
    fun `test if position works as expected`() {
        // GIVEN
        val playerIds = listOf(0, 1, 2, 3, 4, 5)
        val startingLineup = lineupOf(playerIds)
        val mutator = LineupMutator(startingLineup = startingLineup)
        val index = 3

        // WHEN
        val position = mutator.position(playerIdOf(playerIds[index]))

        // THEN
        assert(PlayerPosition.P4 == position)
    }

    @Test
    fun `test if contains works as expected when checking a playerId that is in lineup`() {
        // GIVEN
        val playerIds = listOf(0, 1, 2, 3, 4, 5)
        val startingLineup = lineupOf(playerIds)
        val mutator = LineupMutator(startingLineup = startingLineup)

        // WHEN
        val contains = mutator.contains(playerIdOf(0))

        // THEN
        assert(contains)
    }

    @Test
    fun `test if contains works as expected when checking a playerId that is not in lineup`() {
        // GIVEN
        val playerIds = listOf(0, 1, 2, 3, 4, 5)
        val startingLineup = lineupOf(playerIds)
        val mutator = LineupMutator(startingLineup = startingLineup)

        // WHEN
        val contains = mutator.contains(playerIdOf(6))

        // THEN
        assert(!contains)
    }
}

fun lineupOf(playerIds: List<Int>): Lineup = Lineup.from(playerIds.map { playerIdOf(it) })