package com.kamilh.volleyballstats.presentation.features.players

import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.presentation.features.*
import com.kamilh.volleyballstats.presentation.features.common.Icon
import com.kamilh.volleyballstats.presentation.features.common.SelectOptionState
import com.kamilh.volleyballstats.presentation.features.common.TableContent

data class PlayerStatsState(
    val tableContent: TableContent = TableContent(),
    val selectSkillState: SelectOptionState<StatsSkill>,
    val onFabButtonClicked: () -> Unit,
    override val loadingState: LoadingState = LoadingState(),
    override val topBarState: TopBarState = TopBarState(background = TopBarState.Color.Primary),
    override val actionButton: ActionButton = ActionButton(icon = Icon.Tune),
    override val message: Message? = null,
) : ScreenState
