package com.kamilh.volleyballstats.presentation.features.filter

import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.presentation.features.common.ChooseIntState
import com.kamilh.volleyballstats.presentation.features.common.ChoosePropertiesState
import com.kamilh.volleyballstats.presentation.features.common.SelectOptionState

data class PlayerFiltersState(
    val properties: ChoosePropertiesState<String>,
    val seasonSelectOption: SelectOptionState<Season>,
    val specializationSelectOption: SelectOptionState<Specialization>,
    val teamsSelectOption: SelectOptionState<TeamId>,
    val chooseIntState: ChooseIntState,
    val onApplyButtonClicked: () -> Unit,
)
