package com.kamilh.volleyballstats.ui.screens.filters

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
import com.kamilh.volleyballstats.presentation.features.filter.FiltersPresenter
import com.kamilh.volleyballstats.presentation.features.filter.FiltersState
import com.kamilh.volleyballstats.ui.components.ChooseIntValue
import com.kamilh.volleyballstats.ui.components.ChooseProperties
import com.kamilh.volleyballstats.ui.components.ScreenSkeleton
import com.kamilh.volleyballstats.ui.components.SegmentedControl
import com.kamilh.volleyballstats.ui.components.SelectOption
import com.kamilh.volleyballstats.ui.theme.Dimens

@Composable
fun PlayerFiltersScreen(
    presenter: FiltersPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()
    PlayerFiltersScreen(
        modifier = modifier,
        state = state,
        onItemSelection = presenter::onControlItemSelected,
    )
}

@Composable
private fun PlayerFiltersScreen(
    state: FiltersState,
    modifier: Modifier = Modifier,
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
