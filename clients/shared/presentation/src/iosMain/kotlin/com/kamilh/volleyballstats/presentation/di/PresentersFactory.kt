package com.kamilh.volleyballstats.presentation.di

import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.features.create
import com.kamilh.volleyballstats.presentation.features.filter.FiltersPresenter
import com.kamilh.volleyballstats.presentation.features.home.HomePresenter
import com.kamilh.volleyballstats.presentation.features.main.MainPresenter
import com.kamilh.volleyballstats.presentation.features.savableMapOf
import com.kamilh.volleyballstats.presentation.features.stats.StatsPresenter
import com.kamilh.volleyballstats.presentation.utils.Scope

class PresentersFactory(
    private val appDispatchers: AppDispatchers,
    private val presenterMap: PresenterMap,
) {

    fun createScope(): Scope = Scope(appDispatchers)

    fun createMainPresenter(scope: Scope): MainPresenter = create(
        presenterMap = presenterMap,
        coroutineScope = scope.coroutineScope,
        savableMap = savableMapOf(),
        extras = Unit,
    )

    fun createHomePresenter(scope: Scope): HomePresenter = create(
        presenterMap = presenterMap,
        coroutineScope = scope.coroutineScope,
        savableMap = savableMapOf(),
        extras = Unit,
    )

    fun createStatsPresenter(scope: Scope, statsType: StatsType): StatsPresenter = create(
        presenterMap = presenterMap,
        coroutineScope = scope.coroutineScope,
        savableMap = savableMapOf(),
        extras = statsType,
    )

    fun createFiltersPresenter(scope: Scope, args: FiltersPresenter.Args): FiltersPresenter = create(
        presenterMap = presenterMap,
        coroutineScope = scope.coroutineScope,
        savableMap = savableMapOf(),
        extras = args,
    )
}
