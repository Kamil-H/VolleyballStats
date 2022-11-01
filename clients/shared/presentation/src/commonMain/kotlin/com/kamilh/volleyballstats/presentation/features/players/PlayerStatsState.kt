package com.kamilh.volleyballstats.presentation.features.players

import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.presentation.features.common.SelectOptionState
import com.kamilh.volleyballstats.presentation.features.common.TableContent

data class PlayerStatsState(
    val tableContent: TableContent = TableContent(),
    val selectSkillState: SelectOptionState<StatsSkill>,
    val onFabButtonClicked: () -> Unit,
)
