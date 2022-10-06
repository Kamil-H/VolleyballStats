package com.kamilh.volleyballstats.presentation.features.players.filter

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.PlayerFilters
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

interface PlayerFiltersStorage {

    fun getPlayerFilters(skill: StatsSkill): Flow<PlayerFilters>

    fun toggleProperty(skill: StatsSkill, value: String)

    fun toggleSeason(skill: StatsSkill, season: Season)

    fun toggleSpecialization(skill: StatsSkill, specialization: Specialization)

    fun toggleTeam(skill: StatsSkill, teamId: TeamId)

    fun setNewLimit(skill: StatsSkill, limit: Int)
}

@Inject
@Singleton
class MockPlayerFiltersStorage : PlayerFiltersStorage {

    private val filters = MutableStateFlow<List<FilterUnit>>(emptyList())

    override fun getPlayerFilters(skill: StatsSkill): Flow<PlayerFilters> =
        filters.asStateFlow()
            .map {
                val filterUnits = it.filter { unit -> unit.skill == skill }
                PlayerFilters(
                    selectedProperties = filterUnits.filterByType(FilterUnit.Type.Property).map { unit -> unit.value },
                    selectedSeasons = filterUnits.filterByType(FilterUnit.Type.Season).map { unit -> unit.toSeason() },
                    selectedSpecializations = filterUnits.filterByType(FilterUnit.Type.Specialization).map { unit -> unit.toSpecializations() },
                    selectedTeams = filterUnits.filterByType(FilterUnit.Type.Team).map { unit -> unit.toTeams() },
                    selectedLimit = filterUnits.filterByType(FilterUnit.Type.Limit).map { unit -> unit.toLimit() }.firstOrNull() ?: 0,
                )
            }

    private fun FilterUnit.toSeason(): Season =
        Season.create(value.toInt())

    private fun FilterUnit.toSpecializations(): Specialization =
        Specialization.valueOf(value)

    private fun FilterUnit.toTeams(): TeamId =
        TeamId(value.toLong())

    private fun FilterUnit.toLimit(): Int =
        value.toInt()

    private fun List<FilterUnit>.filterByType(type: FilterUnit.Type): List<FilterUnit> =
        filter { it.type == type }

    override fun toggleProperty(skill: StatsSkill, value: String) {
        filters.toggle(skill, FilterUnit.Type.Property, value)
    }

    override fun toggleSeason(skill: StatsSkill, season: Season) {
        filters.toggle(skill, FilterUnit.Type.Season, season.value.toString())
    }

    override fun toggleSpecialization(skill: StatsSkill, specialization: Specialization) {
        filters.toggle(skill, FilterUnit.Type.Specialization, specialization.name)
    }

    override fun toggleTeam(skill: StatsSkill, teamId: TeamId) {
        filters.toggle(skill, FilterUnit.Type.Team, teamId.value.toString())
    }

    override fun setNewLimit(skill: StatsSkill, limit: Int) {
        filters.update { filterUnits ->
            val type = FilterUnit.Type.Limit
            val unit = filterUnits.find { it.skill == skill && it.type == type }
            filterUnits - listOfNotNull(unit).toSet() + FilterUnit(
                skill = skill,
                type = type,
                value = limit.toString(),
            )
        }
    }

    private fun MutableStateFlow<List<FilterUnit>>.toggle(skill: StatsSkill, type: FilterUnit.Type, value: String) {
        update { current -> current.toggle(skill, type, value) }
    }

    private fun List<FilterUnit>.toggle(skill: StatsSkill, type: FilterUnit.Type, value: String): List<FilterUnit> {
        val unit = findUnit(skill, type, value)
        return if (unit != null) {
            this - unit
        } else {
            this + FilterUnit(
                skill = skill,
                type = type,
                value = value,
            )
        }
    }

    private fun List<FilterUnit>.findUnit(skill: StatsSkill, type: FilterUnit.Type, value: String): FilterUnit? =
        find { it.skill == skill && it.value == value && it.type == type }

    private data class FilterUnit(
        val skill: StatsSkill,
        val type: Type,
        val value: String,
    ) {
        enum class Type {
            Property, Season, Specialization, Team, Limit
        }
    }
}
