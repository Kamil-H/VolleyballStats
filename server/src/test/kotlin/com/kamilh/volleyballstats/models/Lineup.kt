package com.kamilh.volleyballstats.models

fun lineupOf(
    p1: PlayerId = playerIdOf(1),
    p2: PlayerId = playerIdOf(2),
    p3: PlayerId = playerIdOf(3),
    p4: PlayerId = playerIdOf(4),
    p5: PlayerId = playerIdOf(5),
    p6: PlayerId = playerIdOf(6),
): Lineup = Lineup(
    p1 = p1,
    p2 = p2,
    p3 = p3,
    p4 = p4,
    p5 = p5,
    p6 = p6,
)