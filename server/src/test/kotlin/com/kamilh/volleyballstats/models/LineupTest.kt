package com.kamilh.volleyballstats.models

import org.junit.Test

class LineupTest {

    @Test(expected = IllegalStateException::class)
    fun `test whether Lineup fails to construct if not unique values are passed`() {
        Lineup(
            p1 = playerIdOf(0),
            p2 = playerIdOf(0),
            p3 = playerIdOf(0),
            p4 = playerIdOf(0),
            p5 = playerIdOf(0),
            p6 = playerIdOf(0),
        )
    }

    @Test(expected = IllegalStateException::class)
    fun `test whether Lineup fails to construct if lists contains less than 6 values`() {
        Lineup.from(listOf(playerIdOf(0)))
    }

    @Test(expected = IllegalStateException::class)
    fun `test whether Lineup fails to construct if lists contains more than 6 values`() {
        val playerIds = List(size = 7) { playerIdOf(it) }
        Lineup.from(playerIds)
    }

    @Test
    fun `test whether Lineup constructs properly when lists contains exactly 6 unique values`() {
        val playerIds = List(size = 6) { playerIdOf(it) }
        Lineup.from(playerIds)
    }

    @Test
    fun `test whether Lineup constructs properly when we pass exactly 6 unique values`() {
        Lineup(
            p1 = playerIdOf(1),
            p2 = playerIdOf(2),
            p3 = playerIdOf(3),
            p4 = playerIdOf(4),
            p5 = playerIdOf(5),
            p6 = playerIdOf(6),
        )
    }
}