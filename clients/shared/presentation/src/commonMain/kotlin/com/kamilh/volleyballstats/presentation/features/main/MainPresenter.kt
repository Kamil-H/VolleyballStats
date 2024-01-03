package com.kamilh.volleyballstats.presentation.features.main

import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.presentation.features.Presenter
import com.kamilh.volleyballstats.presentation.features.SavableMap
import com.kamilh.volleyballstats.presentation.navigation.NavigationEvent
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventSender
import com.kamilh.volleyballstats.presentation.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class MainPresenter private constructor(
    private val navigationEventSender: NavigationEventSender,
) : Presenter {

    private val _state: MutableStateFlow<MainState> = MutableStateFlow(
        MainState(bottomItems = bottomItems())
    )
    val state: StateFlow<MainState> = _state.asStateFlow()

    private fun bottomItems(selected: BottomMenuItem = BottomMenuItem.Home): List<BottomItemState> =
        BottomMenuItem.entries.map { item ->
            BottomItemState(
                id = item,
                icon = item.icon,
                label = item.label,
                selected = item == selected,
                onClicked = ::onMenuItemClicked,
            )
        }

    private fun onMenuItemClicked(id: BottomMenuItem) {
        val screen = when (id) {
            BottomMenuItem.Home -> Screen.Home
            BottomMenuItem.Players -> Screen.Stats(StatsType.Player)
            BottomMenuItem.Teams -> Screen.Stats(StatsType.Team)
        }
        navigationEventSender.send(NavigationEvent.GoTo(screen))
    }

    fun onTabShown(tabIndex: Int) {
        _state.update { state ->
            state.copy(bottomItems = state.bottomItems.select(tabIndex))
        }
    }

    @Inject
    class Factory(
        private val navigationEventSender: NavigationEventSender,
    ) : Presenter.Factory<MainPresenter, Screen.Main> {

        override fun create(
            coroutineScope: CoroutineScope,
            savableMap: SavableMap,
            screen: Screen.Main,
        ): MainPresenter = MainPresenter(
            navigationEventSender = navigationEventSender,
        )
    }
}
