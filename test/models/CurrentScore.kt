package com.kamilh.models

fun currentScoreOf(
    ownTeam: Int = 0,
    opponentTeam: Int = 0,
): CurrentScore = CurrentScore(
    ownTeam = ownTeam,
    opponentTeam = opponentTeam,
)