package com.kamilh.volleyballstats.presentation.features.players.filter

import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.presentation.features.ChooseIntState
import com.kamilh.volleyballstats.presentation.features.ChoosePropertiesState
import com.kamilh.volleyballstats.presentation.features.SelectOptionState

data class PlayerFiltersState(
    val properties: ChoosePropertiesState<String>,
    val seasonSelectOption: SelectOptionState<Season>,
    val specializationSelectOption: SelectOptionState<Specialization>,
    val teamsSelectOption: SelectOptionState<TeamId>,
    val chooseIntState: ChooseIntState,
)
