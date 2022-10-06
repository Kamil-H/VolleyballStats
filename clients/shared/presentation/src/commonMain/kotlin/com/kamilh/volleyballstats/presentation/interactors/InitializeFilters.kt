package com.kamilh.volleyballstats.presentation.interactors

import com.kamilh.volleyballstats.domain.interactor.NoInputInteractor
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.presentation.extensions.allProperties
import com.kamilh.volleyballstats.presentation.features.players.filter.PlayerFiltersStorage
import me.tatarka.inject.annotations.Inject

typealias InitializeFilters = NoInputInteractor<Unit>

@Inject
class InitializeFiltersInteractor(
    appDispatchers: AppDispatchers,
    private val playerFiltersStorage: PlayerFiltersStorage,
) : InitializeFilters(appDispatchers) {

    @Suppress("MagicNumber")
    override suspend fun doWork() {
        playerFiltersStorage.setAllDefaults(
            latestSeason = Season.create(2021), // TODO: CHANGE IT!
            limit = 5, // TODO: CHANGE IT??
        )
    }

    private fun PlayerFiltersStorage.setAllDefaults(latestSeason: Season, limit: Int) {
        StatsSkill.values().forEach { skill ->
            setDefaults(skill = skill, latestSeason = latestSeason, limit = limit)
        }
    }

    private fun PlayerFiltersStorage.setDefaults(skill: StatsSkill, latestSeason: Season, limit: Int) {
        skill.allProperties.map { it.id }.forEach {
            toggleProperty(skill, it)
        }
        toggleSeason(skill, latestSeason)
        setNewLimit(skill, limit)
    }
}
