package com.kamilh.volleyballstats.domain.models

data class PlayerFilters(
    val selectedProperties: List<String> = emptyList(),
    val selectedSeasons: List<Season> = emptyList(),
    val selectedSpecializations: List<Specialization> = emptyList(),
    val selectedTeams: List<TeamId> = emptyList(),
    val selectedLimit: Int = 0,
)
