package com.kamilh.volleyballstats.presentation.features.players

import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.presentation.features.TableCell
import com.kamilh.volleyballstats.presentation.features.TableContent
import com.kamilh.volleyballstats.presentation.features.TableRow
import com.kamilh.volleyballstats.storage.stats.AttackStats
import com.kamilh.volleyballstats.storage.stats.StatsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

// Move it to commonMain
@Inject
class PlayerStatsPresenter(
    private val attackStats: AttackStats,
    private val coroutineScope: CoroutineScope,
) {

    private val _state: MutableStateFlow<PlayerStatsState> = MutableStateFlow(PlayerStatsState())
    val state: StateFlow<PlayerStatsState> = _state.asStateFlow()

    init {
        val request = AttackStats.Request(
            groupBy = StatsRequest.GroupBy.Player,
            tourId = TourId(2),
            sortBy = AttackStats.Request.SortBy.Efficiency,
        )
        attackStats.getAttackStats(request)
            .onEach { attackStats ->
                _state.update { currentState ->
                    currentState.copy(
                        tableContent = TableContent(
                            rows = attackStats.mapIndexed { index, model -> model.toRow(index) },
                            header = TableRow(
                                cells = listOf(
                                    "index",
                                    "name",
                                    "teamName",
                                    "specialization",
                                    "attempts",
                                    "kill",
                                    "efficiency",
                                    "error",
                                    "pointWinPercent",
                                    "killBreakPoint",
                                    "efficiencyBreakPoint",
                                    "errorBreakPoint",
                                    "killSideOut",
                                    "efficiencySideOut",
                                    "errorSideOut",
                                ).map {
                                    it.toCell()
                                }
                            )
                        )
                    )
                }
            }
            .launchIn(coroutineScope)
    }

    private fun AttackStats.Model.toRow(index: Int): TableRow =
        TableRow(
            cells = listOf(
                (index + 1).toString().toCell(size = TableCell.Size.Small),
                name.toCell(),
                teamName.toCell(),
                specialization.name.toCell(),
                attempts.toString().toCell(),
                kill.toString().toCell(),
                efficiency.toString().toCell(),
                error.toString().toCell(),
                pointWinPercent.toString().toCell(),
                killBreakPoint.toString().toCell(),
                efficiencyBreakPoint.toString().toCell(),
                errorBreakPoint.toString().toCell(),
                killSideOut.toString().toCell(),
                efficiencySideOut.toString().toCell(),
                errorSideOut.toString().toCell(),
            )
        )

    private fun String.toCell(size: TableCell.Size = TableCell.Size.Medium): TableCell =
        TableCell(content = this, size = size)
}
