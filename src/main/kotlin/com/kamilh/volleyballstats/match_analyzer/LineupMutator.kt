package com.kamilh.volleyballstats.match_analyzer

import com.kamilh.volleyballstats.models.*

class LineupMutator(
    startingLineup: Lineup,
    startingRotation: Rotation = Rotation.P1,
) {
    private val _currentLineup: ArrayDeque<PlayerId> = ArrayDeque(6)
    val currentLineup: Lineup
        get() = Lineup(
            p1 = _currentLineup[0],
            p2 = _currentLineup[1],
            p3 = _currentLineup[2],
            p4 = _currentLineup[3],
            p5 = _currentLineup[4],
            p6 = _currentLineup[5],
        )

    private var currentRotation: Rotation = startingRotation

    init {
        startingLineup.toList().forEachIndexed { index, playerId -> _currentLineup.add(index, playerId) }
    }

    fun rotate() {
        currentRotation++
        _currentLineup.add(_currentLineup.removeFirst())
    }

    fun substitution(`in`: PlayerId, `out`: PlayerId) {
        val outIndex = _currentLineup.indexOf(out)
        _currentLineup.removeAt(outIndex)
        _currentLineup.add(outIndex, `in`)
    }

    fun position(playerId: PlayerId): PlayerPosition =
        PlayerPosition.create(_currentLineup.indexOf(playerId) + 1)

    fun contains(playerId: PlayerId): Boolean = _currentLineup.contains(playerId)

    private fun Lineup.toList(): List<PlayerId> = listOf(p1, p2, p3, p4, p5, p6)
}