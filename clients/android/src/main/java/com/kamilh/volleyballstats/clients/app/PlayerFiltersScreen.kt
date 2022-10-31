package com.kamilh.volleyballstats.clients.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kamilh.volleyballstats.presentation.features.players.filter.PlayerFiltersPresenter
import com.kamilh.volleyballstats.ui.components.ChooseIntValue
import com.kamilh.volleyballstats.ui.components.ChooseProperties
import com.kamilh.volleyballstats.ui.components.SelectOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerFiltersScreen(
    modifier: Modifier = Modifier,
    playerFiltersPresenter: PlayerFiltersPresenter,
) {
    val state by playerFiltersPresenter.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Adjust") },
                actions = {
                    Button(onClick = { state.onApplyButtonClicked() }) {
                        Text(text = "Apply")
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .verticalScroll(scrollState)
        ) {
            ChooseProperties(choosePropertiesState = state.properties)
            SelectOption(selectOptionState = state.seasonSelectOption)
            SelectOption(selectOptionState = state.specializationSelectOption)
            SelectOption(selectOptionState = state.teamsSelectOption)
            ChooseIntValue(chooseIntState = state.chooseIntState)
        }
    }

}