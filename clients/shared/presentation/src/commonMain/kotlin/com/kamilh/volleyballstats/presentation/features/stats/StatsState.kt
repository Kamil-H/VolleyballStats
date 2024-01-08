package com.kamilh.volleyballstats.presentation.features.stats

import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.presentation.features.ActionButton
import com.kamilh.volleyballstats.presentation.features.ColorAccent
import com.kamilh.volleyballstats.presentation.features.LoadingState
import com.kamilh.volleyballstats.presentation.features.Message
import com.kamilh.volleyballstats.presentation.features.ScreenState
import com.kamilh.volleyballstats.presentation.features.TopBarState
import com.kamilh.volleyballstats.presentation.features.common.Icon
import com.kamilh.volleyballstats.presentation.features.common.SelectOptionState
import com.kamilh.volleyballstats.presentation.features.common.TableContent

data class StatsState(
    val tableContent: TableContent = TableContent(),
    val selectSkillState: SelectOptionState<StatsSkill>,
    override val loadingState: LoadingState = LoadingState(),
    override val topBarState: TopBarState,
    override val actionButton: ActionButton = ActionButton(icon = Icon.Tune),
    override val message: Message? = null,
    override val colorAccent: ColorAccent,
) : ScreenState
