package com.kamilh.volleyballstats.presentation.features.filter

import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.presentation.features.ActionButton
import com.kamilh.volleyballstats.presentation.features.LoadingState
import com.kamilh.volleyballstats.presentation.features.ScreenState
import com.kamilh.volleyballstats.presentation.features.TopBarState
import com.kamilh.volleyballstats.presentation.features.common.ChooseIntState
import com.kamilh.volleyballstats.presentation.features.common.ChoosePropertiesState
import com.kamilh.volleyballstats.presentation.features.common.SegmentedControlState
import com.kamilh.volleyballstats.presentation.features.common.SelectOptionState

data class PlayerFiltersState(
    val properties: ChoosePropertiesState<String>,
    val seasonSelectOption: SelectOptionState<Season>,
    val specializationSelectOption: SelectOptionState<Specialization>,
    val teamsSelectOption: SelectOptionState<TeamId>,
    val chooseIntState: ChooseIntState,
    val segmentedControlState: SegmentedControlState,
    val showControl: Control,
    val onBackButtonClicked: () -> Unit,
    override val loadingState: LoadingState = LoadingState(),
    override val topBarState: TopBarState,
    override val actionButton: ActionButton = ActionButton(),
) : ScreenState

enum class Control(val itemName: String) {
    Filters("Filters"),
    Properties("Properties"),
}
