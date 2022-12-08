package com.kamilh.volleyballstats.clients.app.ui.screens.filters

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kamilh.volleyballstats.presentation.features.filter.Control
import com.kamilh.volleyballstats.presentation.features.filter.PlayerFiltersPresenter
import com.kamilh.volleyballstats.presentation.features.filter.PlayerFiltersState
import com.kamilh.volleyballstats.ui.components.*
import com.kamilh.volleyballstats.ui.theme.Dimens

@Composable
fun PlayerFiltersScreen(
    modifier: Modifier = Modifier,
    playerFiltersPresenter: PlayerFiltersPresenter,
) {
    val state by playerFiltersPresenter.state.collectAsState()
    PlayerFiltersScreen(
        modifier = modifier,
        state = state,
        onItemSelection = playerFiltersPresenter::onControlItemSelected,
    )
}

@Composable
private fun PlayerFiltersScreen(
    modifier: Modifier = Modifier,
    state: PlayerFiltersState,
    onItemSelection: (selectedItemIndex: Int) -> Unit,
) {
    val scrollState = rememberScrollState()

    ScreenSkeleton(
        state = state,
        modifier = modifier,
        onNavigationButtonClicked = state.onBackButtonClicked,
    ) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            SegmentedControl(
                modifier = Modifier
                    .padding(vertical = Dimens.MarginMedium)
                    .align(alignment = Alignment.CenterHorizontally),
                state = state.segmentedControlState,
                onItemSelection = onItemSelection,
            )
            when (state.showControl) {
                Control.Filters -> {
                    SelectOption(selectOptionState = state.seasonSelectOption)
                    SelectOption(selectOptionState = state.specializationSelectOption)
                    SelectOption(selectOptionState = state.teamsSelectOption)
                    ChooseIntValue(chooseIntState = state.chooseIntState)
                }
                Control.Properties -> {
                    ChooseProperties(choosePropertiesState = state.properties)
                }
            }
        }
    }
}
