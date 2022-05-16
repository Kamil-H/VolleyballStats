package com.kamilh.volleyballstats.repository.models.mappers

// /games/id/1101158.html
fun String.extractGameId(): Long? = extractId()

// /players/id/27975.html
fun String.extractPlayerId(): Long? = extractId()

// /teams/id/30288.html
fun String.extractTeamId(): Long? = extractId()

// /whatever/id/<ID>.html
private fun String.extractId(): Long? {
    val segments = replace(".html", "")
        .split("/")
    val idIndex = segments.indexOf("id")
    return segments.getOrNull(index = idIndex + 1)?.toLongOrNull()
}