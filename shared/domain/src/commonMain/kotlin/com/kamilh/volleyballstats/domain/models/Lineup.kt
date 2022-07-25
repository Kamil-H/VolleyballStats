package com.kamilh.volleyballstats.domain.models

data class Lineup(
    val p1: PlayerId,
    val p2: PlayerId,
    val p3: PlayerId,
    val p4: PlayerId,
    val p5: PlayerId,
    val p6: PlayerId,
) {

    init {
        val set = setOf(p1, p2, p3, p4, p5, p6)
         if (set.size != NUMBER_OF_ITEMS) {
             error("Please provide unique PlayerIds: $set")
         }
    }

    override fun toString() = "[p1: ${p1.value}, p2: ${p2.value}, p3: ${p3.value}, p4: ${p4.value}, p5: ${p5.value}, p6: ${p6.value}]"

    companion object {
        private const val NUMBER_OF_ITEMS = 6

        @Suppress("MagicNumber")
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
