package com.kamilh.volleyballstats.presentation.features.players

import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.presentation.extensions.findProperty
import com.kamilh.volleyballstats.presentation.features.common.*
import com.kamilh.volleyballstats.presentation.features.players.properties.*
import com.kamilh.volleyballstats.storage.stats.*
import me.tatarka.inject.annotations.Inject

interface StatsModelMapper {

    fun map(
        stats: List<StatsModel>,
        properties: List<Property<String>>,
        selectedProperty: Property<String>,
        callback: (Property<String>) -> Unit,
    ): TableContent
}

@Inject
class StatsModelMapperImpl : StatsModelMapper {

    override fun map(
        stats: List<StatsModel>,
        properties: List<Property<String>>,
        selectedProperty: Property<String>,
        callback: (Property<String>) -> Unit,
    ): TableContent = TableContent(
        rows = stats.mapIndexed { index, model -> model.toRow(index, properties) },
        header = HeaderRow(
            cells = properties.mapIndexed { index, property ->
                property.toHeaderCell(
                    size = if (index == 0) CellSize.Small else CellSize.Medium,
                    selectedProperty = selectedProperty,
                    onClicked = { callback(it) }
                )
            }
        )
    )

    private fun Property<String>.toHeaderCell(
        size: CellSize = CellSize.Medium,
        selectedProperty: Property<String>,
        onClicked: (Property<String>) -> Unit,
    ): HeaderCell = HeaderCell(
        firstLine = shortName,
        secondLine = additionalName,
        size = size,
        selected = this.id == selectedProperty.id,
        onClick = if (filterable) {
            { onClicked(this) }
        } else null
    )

    private fun StatsModel.toRow(index: Int, properties: List<Property<String>>): DataRow =
        TableRow(cells = properties.map { toValueCell(index, it) })

    private fun StatsModel.toValueCell(index: Int, property: Property<String>): DataCell =
        when (this) {
            is AttackStatsStorage.Model -> findProperty<AttackProperty>(property).toValueCell(index, this)
            is BlockStatsStorage.Model -> findProperty<BlockProperty>(property).toValueCell(index, this)
            is ReceiveStatsStorage.Model -> findProperty<ReceiveProperty>(property).toValueCell(index, this)
            is DigStatsStorage.Model -> findProperty<DigProperty>(property).toValueCell(index, this)
            is SetStatsStorage.Model -> findProperty<SetProperty>(property).toValueCell(index, this)
            is ServeStatsStorage.Model -> findProperty<ServeProperty>(property).toValueCell(index, this)
        }

    private fun AttackProperty.toValueCell(index: Int, model: AttackStatsStorage.Model): DataCell =
        when (this) {
            AttackProperty.Index -> (index + 1).toString().toDataCell(size = CellSize.Small)
            AttackProperty.Name -> model.name.shorterName().toDataCell()
            AttackProperty.TeamName -> model.teamName.toDataCell()
            AttackProperty.FullTeamName -> model.fullTeamName.toDataCell()
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

    private fun BlockProperty.toValueCell(index: Int, model: BlockStatsStorage.Model): DataCell =
        when (this) {
            BlockProperty.Index -> (index + 1).toString().toDataCell(size = CellSize.Small)
            BlockProperty.Name -> model.name.shorterName().toDataCell()
            BlockProperty.TeamName -> model.teamName.toDataCell()
            BlockProperty.FullTeamName -> model.fullTeamName.toDataCell()
            BlockProperty.Specialization -> model.specialization.shortName.toDataCell()
            BlockProperty.Attempts -> model.attempts.toString().toDataCell()
            BlockProperty.Kill -> model.kill.toString().toDataCell()
            BlockProperty.KillPerAttempt -> model.killPerAttempt.toString().toDataCell()
            BlockProperty.Rebound -> model.rebound.toString().toDataCell()
            BlockProperty.ReboundPerAttempt -> model.reboundPerAttempt.toString().toDataCell()
            BlockProperty.KillPlusRebound -> model.killPlusRebound.toString().toDataCell()
            BlockProperty.KillPlusReboundPerAttempt -> model.killPlusReboundPerAttempt.toString()
                .toDataCell()
            BlockProperty.Error -> model.error.toString().toDataCell()
            BlockProperty.ErrorPerAttempt -> model.errorPerAttempt.toString().toDataCell()
            BlockProperty.PointWinPercent -> model.pointWinPercent.toString().toDataCell()
        }

    private fun DigProperty.toValueCell(index: Int, model: DigStatsStorage.Model): DataCell =
        when (this) {
            DigProperty.Index -> (index + 1).toString().toDataCell(size = CellSize.Small)
            DigProperty.Name -> model.name.shorterName().toDataCell()
            DigProperty.TeamName -> model.teamName.toDataCell()
            DigProperty.FullTeamName -> model.fullTeamName.toDataCell()
            DigProperty.Specialization -> model.specialization.shortName.toDataCell()
            DigProperty.Attempts -> model.attempts.toString().toDataCell()
            DigProperty.Digs -> model.digs.toString().toDataCell()
            DigProperty.SuccessPercent -> model.successPercent.toString().toDataCell()
            DigProperty.Errors -> model.errors.toString().toDataCell()
            DigProperty.PointWinPercent -> model.pointWinPercent.toString().toDataCell()
        }

    private fun ReceiveProperty.toValueCell(index: Int, model: ReceiveStatsStorage.Model): DataCell =
        when (this) {
            ReceiveProperty.Index -> (index + 1).toString().toDataCell(size = CellSize.Small)
            ReceiveProperty.Name -> model.name.shorterName().toDataCell()
            ReceiveProperty.TeamName -> model.teamName.toDataCell()
            ReceiveProperty.FullTeamName -> model.fullTeamName.toDataCell()
            ReceiveProperty.Specialization -> model.specialization.shortName.toDataCell()
            ReceiveProperty.Attempts -> model.attempts.toString().toDataCell()
            ReceiveProperty.Perfect -> model.perfect.toString().toDataCell()
            ReceiveProperty.PerfectPositive -> model.perfectPositive.toString().toDataCell()
            ReceiveProperty.Efficiency -> model.efficiency.toString().toDataCell()
            ReceiveProperty.Errors -> model.errors.toString().toDataCell()
            ReceiveProperty.ErrorsPercent -> model.errorsPercent.toString().toDataCell()
            ReceiveProperty.SideOut -> model.sideOut.toString().toDataCell()
            ReceiveProperty.PointWinPercent -> model.pointWinPercent.toString().toDataCell()
        }

    private fun ServeProperty.toValueCell(index: Int, model: ServeStatsStorage.Model): DataCell =
        when (this) {
            ServeProperty.Index -> (index + 1).toString().toDataCell(size = CellSize.Small)
            ServeProperty.Name -> model.name.shorterName().toDataCell()
            ServeProperty.TeamName -> model.teamName.toDataCell()
            ServeProperty.FullTeamName -> model.fullTeamName.toDataCell()
            ServeProperty.Specialization -> model.specialization.shortName.toDataCell()
            ServeProperty.Attempts -> model.attempts.toString().toDataCell()
            ServeProperty.Efficiency -> model.efficiency.toString().toDataCell()
            ServeProperty.Ace -> model.ace.toString().toDataCell()
            ServeProperty.AcePercent -> model.acePercent.toString().toDataCell()
            ServeProperty.Freeball -> model.freeball.toString().toDataCell()
            ServeProperty.FreeballPercent -> model.freeballPercent.toString().toDataCell()
            ServeProperty.AceFreeball -> model.aceFreeball.toString().toDataCell()
            ServeProperty.AceFreeballPercent -> model.aceFreeballPercent.toString().toDataCell()
            ServeProperty.Errors -> model.errors.toString().toDataCell()
            ServeProperty.ErrorsPercent -> model.errorsPercent.toString().toDataCell()
            ServeProperty.PointWinPercent -> model.pointWinPercent.toString().toDataCell()
        }

    private fun SetProperty.toValueCell(index: Int, model: SetStatsStorage.Model): DataCell =
        when (this) {
            SetProperty.Index -> (index + 1).toString().toDataCell(size = CellSize.Small)
            SetProperty.Name -> model.name.shorterName().toDataCell()
            SetProperty.TeamName -> model.teamName.toDataCell()
            SetProperty.FullTeamName -> model.fullTeamName.toDataCell()
            SetProperty.Specialization -> model.specialization.shortName.toDataCell()
            SetProperty.Attempts -> model.attempts.toString().toDataCell()
            SetProperty.Perfect -> model.perfect.toString().toDataCell()
            SetProperty.PerfectPositive -> model.perfectPositive.toString().toDataCell()
            SetProperty.Efficiency -> model.efficiency.toString().toDataCell()
            SetProperty.Errors -> model.errors.toString().toDataCell()
            SetProperty.ErrorsPercent -> model.errorsPercent.toString().toDataCell()
            SetProperty.PointWinPercent -> model.pointWinPercent.toString().toDataCell()
        }

    private fun String.toDataCell(size: CellSize = CellSize.Medium): DataCell =
        DataCell(content = this, size = size)

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
}
