package com.kamilh.volleyballstats.clients.app.ui.screens.filters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kamilh.volleyballstats.presentation.features.filter.PlayerFiltersPresenter
import com.kamilh.volleyballstats.presentation.features.filter.PlayerFiltersState
import com.kamilh.volleyballstats.ui.components.ChooseIntValue
import com.kamilh.volleyballstats.ui.components.ChooseProperties
import com.kamilh.volleyballstats.ui.components.ScreenSkeleton
import com.kamilh.volleyballstats.ui.components.SelectOption

@Composable
fun PlayerFiltersScreen(
    modifier: Modifier = Modifier,
    playerFiltersPresenter: PlayerFiltersPresenter,
) {
    val state by playerFiltersPresenter.state.collectAsState()
    PlayerFiltersScreen(
        modifier = modifier,
        state = state,
    )
}

@Composable
private fun PlayerFiltersScreen(
    modifier: Modifier = Modifier,
    state: PlayerFiltersState,
) {
    val scrollState = rememberScrollState()

    ScreenSkeleton(
        state = state,
        modifier = modifier,
        onNavigationButtonClicked = state.onBackButtonClicked,
    ) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            ChooseProperties(choosePropertiesState = state.properties)
            SelectOption(selectOptionState = state.seasonSelectOption)
            SelectOption(selectOptionState = state.specializationSelectOption)
            SelectOption(selectOptionState = state.teamsSelectOption)
            ChooseIntValue(chooseIntState = state.chooseIntState)
        }
    }
}
