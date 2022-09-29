package com.kamilh.volleyballstats.presentation.features.players

import com.kamilh.volleyballstats.domain.models.Skill
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.presentation.features.*
import com.kamilh.volleyballstats.storage.stats.AttackStats
import com.kamilh.volleyballstats.storage.stats.StatsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

@Inject
class PlayerStatsPresenter(
    private val attackStats: AttackStats,
    private val coroutineScope: CoroutineScope,
) {

    private val _state: MutableStateFlow<PlayerStatsState> = MutableStateFlow(
        PlayerStatsState(
            selectSkillState = SelectOptionState(options = Skill.values().map { skill ->
                SelectOptionState.Option(
                    id = skill,
                    label = skill.name,
                    selected = skill == Skill.Attack,
                )
            }, onSelected = ::onSkillClicked)
        )
    )
    val state: StateFlow<PlayerStatsState> = _state.asStateFlow()

    private val _configuration: MutableStateFlow<AttackConfiguration> = MutableStateFlow(AttackConfiguration())

    init {
        val request = AttackStats.Request(
            groupBy = StatsRequest.GroupBy.Player,
            tourId = TourId(2),
            sortBy = AttackStats.Request.SortBy.Efficiency,
        )
        attackStats.getAttackStats(request)
            .combine(_configuration) { attackStats, configuration ->
                _state.update { currentState ->
                    currentState.copy(
                        tableContent = TableContent(
                            rows = attackStats.mapIndexed { index, model ->
                                model.toRow(index, configuration.properties)
                            },
                            header = HeaderRow(cells = configuration.properties.map { it.toHeaderCell() })
                        )
                    )
                }
            }
            .launchIn(coroutineScope)
    }

    private fun AttackStats.Model.toRow(index: Int, properties: List<AttackProperty>): DataRow =
        TableRow(cells = properties.map { it.toValueCell(index, this) })

    private fun String.toDataCell(size: CellSize = CellSize.Medium): DataCell =
        DataCell(content = this, size = size)

    private fun AttackProperty.toHeaderCell(size: CellSize = CellSize.Medium): HeaderCell =
        HeaderCell(firstLine = shortName, secondLine = additionalName, size = size)

    private fun AttackProperty.toValueCell(index: Int, model: AttackStats.Model): DataCell =
        when (this) {
            AttackProperty.Index -> (index + 1).toString().toDataCell(size = CellSize.Small)
            AttackProperty.Name -> model.name.shorterName().toDataCell()
            AttackProperty.TeamName -> model.teamName.toDataCell()
            AttackProperty.Specialization -> model.specialization.shortName.toDataCell()
            AttackProperty.Attempts -> model.attempts.toString().toDataCell()
            AttackProperty.Kill -> model.kill.toString().toDataCell()
            AttackProperty.Efficiency -> model.efficiency.toString().toDataCell()
            AttackProperty.Error -> model.error.toString().toDataCell()
            AttackProperty.PointWinPercent -> model.pointWinPercent.toString().toDataCell()
            AttackProperty.KillBreakPoint -> model.killBreakPoint.toString().toDataCell()
            AttackProperty.EfficiencyBreakPoint -> model.efficiencyBreakPoint.toString().toDataCell()
            AttackProperty.ErrorBreakPoint -> model.errorBreakPoint.toString().toDataCell()
            AttackProperty.KillSideOut -> model.killSideOut.toString().toDataCell()
            AttackProperty.EfficiencySideOut -> model.efficiencySideOut.toString().toDataCell()
            AttackProperty.ErrorSideOut -> model.errorSideOut.toString().toDataCell()
        }

    private val Specialization.shortName: String
        get() = when (this) {
            Specialization.Setter -> "S"
            Specialization.Libero -> "L"
            Specialization.MiddleBlocker -> "MB"
            Specialization.OutsideHitter -> "OH"
            Specialization.OppositeHitter -> "OP"
        }

    private fun String.shorterName(): String =
        split(" ")
            .let { listOf(it.last()) + it.dropLast(1) }
            .mapIndexed { index, s -> (if (index == 0) s else s.first() + ".") + " " }
            .joinToString(separator = "") { it }
            .trim()

    private fun onSkillClicked(id: Skill) {
        _state.update { state ->
            state.copy(selectSkillState = state.selectSkillState.selectSingle(id))
        }
    }
}
