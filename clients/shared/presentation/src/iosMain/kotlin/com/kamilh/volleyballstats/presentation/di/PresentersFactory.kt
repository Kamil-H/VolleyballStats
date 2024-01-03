package com.kamilh.volleyballstats.presentation.di

import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import com.kamilh.volleyballstats.presentation.features.PresenterMap
import com.kamilh.volleyballstats.presentation.features.create
import com.kamilh.volleyballstats.presentation.features.filter.FiltersPresenter
import com.kamilh.volleyballstats.presentation.features.home.HomePresenter
import com.kamilh.volleyballstats.presentation.features.main.MainPresenter
import com.kamilh.volleyballstats.presentation.features.savableMapOf
import com.kamilh.volleyballstats.presentation.features.stats.StatsPresenter
import com.kamilh.volleyballstats.presentation.navigation.Screen
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
        screen = Screen.Main,
    )

    fun createHomePresenter(scope: Scope): HomePresenter = create(
        presenterMap = presenterMap,
        coroutineScope = scope.coroutineScope,
        savableMap = savableMapOf(),
        screen = Screen.Home,
    )

    fun createStatsPresenter(scope: Scope, screen: Screen.Stats): StatsPresenter = create(
        presenterMap = presenterMap,
        coroutineScope = scope.coroutineScope,
        savableMap = savableMapOf(),
        screen = screen,
    )

    fun createFiltersPresenter(scope: Scope, screen: Screen.Filters): FiltersPresenter = create(
        presenterMap = presenterMap,
        coroutineScope = scope.coroutineScope,
        savableMap = savableMapOf(),
        screen = screen,
    )
}
