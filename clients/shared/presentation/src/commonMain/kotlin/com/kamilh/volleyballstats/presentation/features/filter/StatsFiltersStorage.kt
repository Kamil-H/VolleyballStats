package com.kamilh.volleyballstats.presentation.features.filter

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.StatsFilters
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.domain.models.stats.StatsType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

interface StatsFiltersStorage {

    fun getStatsFilters(skill: StatsSkill, statsType: StatsType): Flow<StatsFilters>

    fun toggleProperty(skill: StatsSkill, statsType: StatsType, value: String)

    fun toggleSeason(skill: StatsSkill, statsType: StatsType, season: Season)

    fun toggleSpecialization(skill: StatsSkill, statsType: StatsType, specialization: Specialization)

    fun toggleTeam(skill: StatsSkill, statsType: StatsType, teamId: TeamId)

    fun setNewLimit(skill: StatsSkill, statsType: StatsType, limit: Int)
}

@Inject
@Singleton
class MockStatsFiltersStorage : StatsFiltersStorage {

    private val filters = MutableStateFlow<List<FilterUnit>>(emptyList())

    override fun getStatsFilters(skill: StatsSkill, statsType: StatsType): Flow<StatsFilters> =
        filters.asStateFlow().map {
            it.filter { unit ->
                unit.skill == skill && unit.skillType == statsType
            }.toFilters()
        }

    private fun List<FilterUnit>.toFilters(): StatsFilters =
        StatsFilters(
            selectedProperties = filterByType(FilterUnit.Type.Property) { unit -> unit.value },
            selectedSeasons = filterByType(FilterUnit.Type.Season) { unit -> unit.toSeason() },
            selectedSpecializations = filterByType(FilterUnit.Type.Specialization) { unit -> unit.toSpecializations() },
            selectedTeams = filterByType(FilterUnit.Type.Team) { unit -> unit.toTeams() },
            selectedLimit = filterByType(FilterUnit.Type.Limit) { unit -> unit.toLimit() }.firstOrNull() ?: 0,
        )

    private fun FilterUnit.toSeason(): Season =
        Season.create(value.toInt())

    private fun FilterUnit.toSpecializations(): Specialization =
        Specialization.valueOf(value)

    private fun FilterUnit.toTeams(): TeamId =
        TeamId(value.toLong())

    private fun FilterUnit.toLimit(): Int =
        value.toInt()

    private fun <T> List<FilterUnit>.filterByType(type: FilterUnit.Type, transform: (FilterUnit) -> T): List<T> =
        filter { it.type == type }.map { transform(it) }

    override fun toggleProperty(skill: StatsSkill, statsType: StatsType, value: String) {
        filters.toggle(
            skill = skill,
            type = FilterUnit.Type.Property,
            value = value,
            statsType = statsType,
        )
    }

    override fun toggleSeason(skill: StatsSkill, statsType: StatsType, season: Season) {
        filters.toggle(
            skill = skill,
            type = FilterUnit.Type.Season,
            value = season.value.toString(),
            statsType = statsType,
        )
    }

    override fun toggleSpecialization(skill: StatsSkill, statsType: StatsType, specialization: Specialization) {
        filters.toggle(
            skill = skill,
            type = FilterUnit.Type.Specialization,
            value = specialization.name,
            statsType = statsType,
        )
    }

    override fun toggleTeam(skill: StatsSkill, statsType: StatsType, teamId: TeamId) {
        filters.toggle(
            skill = skill,
            type = FilterUnit.Type.Team,
            value = teamId.value.toString(),
            statsType = statsType,
        )
    }

    override fun setNewLimit(skill: StatsSkill, statsType: StatsType, limit: Int) {
        filters.update { filterUnits ->
            val type = FilterUnit.Type.Limit
            val unit = filterUnits.find { it.skill == skill && it.type == type }
            filterUnits - listOfNotNull(unit).toSet() + FilterUnit(
                skill = skill,
                type = type,
                value = limit.toString(),
                skillType = statsType,
            )
        }
    }

    private fun MutableStateFlow<List<FilterUnit>>.toggle(
        skill: StatsSkill,
        type: FilterUnit.Type,
        value: String,
        statsType: StatsType,
    ) {
        update { current -> current.toggle(skill, type, value, statsType) }
    }

    private fun List<FilterUnit>.toggle(
        skill: StatsSkill,
        type: FilterUnit.Type,
        value: String,
        statsType: StatsType,
    ): List<FilterUnit> {
        val unit = findUnit(skill, type, value, statsType)
        return if (unit != null) {
            this - unit
        } else {
            this + FilterUnit(
                skill = skill,
                type = type,
                value = value,
                skillType = statsType,
            )
        }
    }

    private fun List<FilterUnit>.findUnit(
        skill: StatsSkill,
        type: FilterUnit.Type,
        value: String,
        statsType: StatsType,
    ): FilterUnit? = find {
        it.skill == skill && it.value == value && it.type == type && it.skillType == statsType
    }

    private data class FilterUnit(
        val skill: StatsSkill,
        val skillType: StatsType,
        val type: Type,
        val value: String,
    ) {
        enum class Type {
            Property, Season, Specialization, Team, Limit
        }
    }
}
