package com.kamilh.models

data class Lineup(
    val p1: PlayerId,
    val p2: PlayerId,
    val p3: PlayerId,
    val p4: PlayerId,
    val p5: PlayerId,
    val p6: PlayerId,
) {

    init {
         if (setOf(p1, p2, p3, p4, p5, p6).size != NUMBER_OF_ITEMS) {
             error("Please provide unique PlayerIds")
         }
    }

    companion object {
        private const val NUMBER_OF_ITEMS = 6

        fun from(playerIds: List<PlayerId>): Lineup =
            if (playerIds.size == NUMBER_OF_ITEMS) {
                Lineup(
                    p1 = playerIds[0],
                    p2 = playerIds[1],
                    p3 = playerIds[2],
                    p4 = playerIds[3],
                    p5 = playerIds[4],
                    p6 = playerIds[5],
                )
            } else {
                error("Wrong playerIds size: ${playerIds.size}. Expected: $NUMBER_OF_ITEMS")
            }
    }
}

fun Lineup.toList(): List<PlayerId> = listOf(p1, p2, p3, p4, p5, p6)