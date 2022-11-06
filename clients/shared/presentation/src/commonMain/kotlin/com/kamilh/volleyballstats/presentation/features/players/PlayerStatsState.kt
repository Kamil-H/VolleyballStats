package com.kamilh.volleyballstats.presentation.features.players

import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.presentation.features.common.SelectOptionState
import com.kamilh.volleyballstats.presentation.features.common.TableContent

data class PlayerStatsState(
    val tableContent: TableContent = TableContent(),
    val selectSkillState: SelectOptionState<StatsSkill>,
    val loadingState: LoadingState? = null,
    val showFullScreenLoading: Boolean = false,
    val showSmallLoading: Boolean = false,
    val showFab: Boolean = false,
    val onFabButtonClicked: () -> Unit,
)

data class LoadingState(val text: String)
