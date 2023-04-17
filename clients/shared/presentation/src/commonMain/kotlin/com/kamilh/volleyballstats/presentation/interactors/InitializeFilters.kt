package com.kamilh.volleyballstats.presentation.interactors

import com.kamilh.volleyballstats.domain.interactor.NoInputInteractor
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.presentation.extensions.allProperties
import com.kamilh.volleyballstats.presentation.features.filter.StatsFiltersStorage
import com.kamilh.volleyballstats.storage.TourStorage
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject

typealias InitializeFilters = NoInputInteractor<Unit>

@Inject
class InitializeFiltersInteractor(
    appDispatchers: AppDispatchers,
    private val statsFiltersStorage: StatsFiltersStorage,
    private val tourStorage: TourStorage,
) : InitializeFilters(appDispatchers) {

    @Suppress("MagicNumber")
    override suspend fun doWork() {
        statsFiltersStorage.setAllDefaults(
            latestSeason = tourStorage.getLatestSeason().filterNotNull().first(),
            limit = 5, // TODO: CHANGE IT??
        )
    }

    private fun StatsFiltersStorage.setAllDefaults(latestSeason: Season, limit: Int) {
        StatsSkill.values().forEach { skill ->
            setDefaults(skill = skill, latestSeason = latestSeason, limit = limit)
        }
    }

    private fun StatsFiltersStorage.setDefaults(skill: StatsSkill, latestSeason: Season, limit: Int) {
        StatsType.values().forEach { type ->
            skill.allProperties(type).map { it.id }.forEach {
                toggleProperty(skill, type, it)
            }
            toggleSeason(skill, type, latestSeason)
            setNewLimit(skill, type, limit)
        }
    }
}
