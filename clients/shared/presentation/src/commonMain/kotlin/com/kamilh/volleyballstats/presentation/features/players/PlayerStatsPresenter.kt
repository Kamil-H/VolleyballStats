package com.kamilh.volleyballstats.presentation.features.players

import com.kamilh.volleyballstats.domain.models.PlayerFilters
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.presentation.extensions.allProperties
import com.kamilh.volleyballstats.presentation.features.*
import com.kamilh.volleyballstats.presentation.features.players.filter.PlayerFiltersStorage
import com.kamilh.volleyballstats.presentation.features.players.properties.*
import com.kamilh.volleyballstats.storage.stats.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

@Inject
class PlayerStatsPresenter(
    private val attackStatsStorage: AttackStatsStorage,
    private val blockStatsStorage: BlockStatsStorage,
    private val digStatsStorage: DigStatsStorage,
    private val receiveStatsStorage: ReceiveStatsStorage,
    private val serveStatsStorage: ServeStatsStorage,
    private val setStatsStorage: SetStatsStorage,
    private val coroutineScope: CoroutineScope,
    private val playerFiltersStorage: PlayerFiltersStorage,
) {

    private val chosenSkill = MutableStateFlow(StatsSkill.Attack)
    private val _state: MutableStateFlow<PlayerStatsState> = MutableStateFlow(
        PlayerStatsState(
            selectSkillState = SelectOptionState(options = StatsSkill.values().map { skill ->
                SelectOptionState.Option(
                    id = skill,
                    label = skill.name,
                    selected = skill == chosenSkill.value,
                )
            }, onSelected = ::onSkillClicked)
        )
    )
    val state: StateFlow<PlayerStatsState> = _state.asStateFlow()

    private val sortBy: MutableStateFlow<Map<StatsSkill, Property<String>>> = MutableStateFlow(
        StatsSkill.values().associateWith { it.getDefaultSort() }
    )

    init {
        val sortByProperty = sortBy.combine(chosenSkill) { sortBy, skill -> sortBy[skill]!! }
        playerFiltersStorage
            .request(chosenSkill, sortBy)
            .flatMapLatest { requestWithProperties ->
                requestWithProperties.statsRequest.toModelFlow().map {
                    it to requestWithProperties.properties
                }
            }
            .combine(sortByProperty) { statsPropertiesPair, sortBy ->
                _state.update { currentState ->
                    currentState.produceNewState(statsPropertiesPair.first, statsPropertiesPair.second, sortBy)
                }
            }
            .launchIn(coroutineScope)
    }

    private fun PlayerStatsState.produceNewState(
        stats: List<StatsModel>,
        properties: List<Property<String>>,
        selectedProperty: Property<String>,
    ): PlayerStatsState = copy(
        tableContent = TableContent(
            rows = stats.mapIndexed { index, model ->
                model.toRow(index, properties)
            },
            header = HeaderRow(
                cells = properties.mapIndexed { index, property ->
                    property.toHeaderCell(
                        size = if (index == 0) CellSize.Small else CellSize.Medium,
                        selectedProperty = selectedProperty,
                    ) { newSelectedProperty ->
                        sortBy.update { map ->
                            map.toMutableMap().apply {
                                this[chosenSkill.value] = newSelectedProperty
                            }
                        }
                    }
                }
            )
        )
    )

    private fun StatsRequest.toModelFlow(): Flow<List<StatsModel>> =
        when (this) {
            is AttackStatsStorage.Request -> attackStatsStorage.getStats(this)
            is BlockStatsStorage.Request -> blockStatsStorage.getStats(this)
            is DigStatsStorage.Request -> digStatsStorage.getStats(this)
            is ReceiveStatsStorage.Request -> receiveStatsStorage.getStats(this)
            is ServeStatsStorage.Request -> serveStatsStorage.getStats(this)
            is SetStatsStorage.Request -> setStatsStorage.getStats(this)
        }

    private fun onSkillClicked(id: StatsSkill) {
        _state.update { state ->
            state.copy(selectSkillState = state.selectSkillState.selectSingle(id))
        }
        chosenSkill.value = id
    }
}

private class RequestWithProperties(
    val statsRequest: StatsRequest,
    val properties: List<Property<String>>
)

private fun PlayerFiltersStorage.request(
    skillFlow: Flow<StatsSkill>,
    sortBy: Flow<Map<StatsSkill, Property<String>>>,
): Flow<RequestWithProperties> =
    skillFlow.flatMapLatest { skill ->
        getPlayerFilters(skill).combine(sortBy) { playerFilters, sortBy ->
            RequestWithProperties(
                statsRequest = playerFilters.toRequest(skill, sortBy),
                properties = skill.allProperties(playerFilters.selectedProperties)
            )
        }
    }

private fun PlayerFilters.toRequest(
    skill: StatsSkill,
    sortBy: Map<StatsSkill, Property<String>>,
): StatsRequest = toRequest(skill = skill, sortBy = sortBy[skill]!!)

@Suppress("LongMethod")
private fun PlayerFilters.toRequest(skill: StatsSkill, sortBy: Property<String>): StatsRequest =
    when (skill) {
        StatsSkill.Attack -> AttackStatsStorage.Request(
            groupBy = StatsRequest.GroupBy.Player,
            sortBy = AttackProperty.values().first { it.id == sortBy.id }.toSortBy(),
            seasons = selectedSeasons,
            specializations = selectedSpecializations,
            teams = selectedTeams,
            minAttempts = selectedLimit.toLong(),
        )
        StatsSkill.Block -> BlockStatsStorage.Request(
            groupBy = StatsRequest.GroupBy.Player,
            sortBy = BlockProperty.values().first { it.id == sortBy.id }.toSortBy(),
            seasons = selectedSeasons,
            specializations = selectedSpecializations,
            teams = selectedTeams,
            minAttempts = selectedLimit.toLong(),
        )
        StatsSkill.Dig -> DigStatsStorage.Request(
            groupBy = StatsRequest.GroupBy.Player,
            sortBy = DigProperty.values().first { it.id == sortBy.id }.toSortBy(),
            seasons = selectedSeasons,
            specializations = selectedSpecializations,
            teams = selectedTeams,
            minAttempts = selectedLimit.toLong(),
        )
        StatsSkill.Set -> SetStatsStorage.Request(
            groupBy = StatsRequest.GroupBy.Player,
            sortBy = SetProperty.values().first { it.id == sortBy.id }.toSortBy(),
            seasons = selectedSeasons,
            specializations = selectedSpecializations,
            teams = selectedTeams,
            minAttempts = selectedLimit.toLong(),
        )
        StatsSkill.Receive -> ReceiveStatsStorage.Request(
            groupBy = StatsRequest.GroupBy.Player,
            sortBy = ReceiveProperty.values().first { it.id == sortBy.id }.toSortBy(),
            seasons = selectedSeasons,
            specializations = selectedSpecializations,
            teams = selectedTeams,
            minAttempts = selectedLimit.toLong(),
        )
        StatsSkill.Serve -> ServeStatsStorage.Request(
            groupBy = StatsRequest.GroupBy.Player,
            sortBy = ServeProperty.values().first { it.id == sortBy.id }.toSortBy(),
            seasons = selectedSeasons,
            specializations = selectedSpecializations,
            teams = selectedTeams,
            minAttempts = selectedLimit.toLong(),
        )
    }

private fun AttackProperty.toSortBy(): AttackStatsStorage.Request.SortBy =
    when (this) {
        AttackProperty.Index, AttackProperty.Name, AttackProperty.TeamName,
        AttackProperty.Specialization -> propertyError(this)
        AttackProperty.Attempts -> AttackStatsStorage.Request.SortBy.Attempts
        AttackProperty.Kill -> AttackStatsStorage.Request.SortBy.Kill
        AttackProperty.Efficiency -> AttackStatsStorage.Request.SortBy.Efficiency
        AttackProperty.Error -> AttackStatsStorage.Request.SortBy.Error
        AttackProperty.PointWinPercent -> AttackStatsStorage.Request.SortBy.PointWinPercent
        AttackProperty.KillBreakPoint -> AttackStatsStorage.Request.SortBy.KillBreakPoint
        AttackProperty.EfficiencyBreakPoint -> AttackStatsStorage.Request.SortBy.EfficiencyBreakPoint
        AttackProperty.ErrorBreakPoint -> AttackStatsStorage.Request.SortBy.ErrorBreakPoint
        AttackProperty.KillSideOut -> AttackStatsStorage.Request.SortBy.KillSideOut
        AttackProperty.EfficiencySideOut -> AttackStatsStorage.Request.SortBy.EfficiencySideOut
        AttackProperty.ErrorSideOut -> AttackStatsStorage.Request.SortBy.ErrorSideOut
    }

private fun BlockProperty.toSortBy(): BlockStatsStorage.Request.SortBy =
    when (this) {
        BlockProperty.Index, BlockProperty.Name, BlockProperty.TeamName,
        BlockProperty.Specialization -> propertyError(this)
        BlockProperty.Attempts -> BlockStatsStorage.Request.SortBy.Attempts
        BlockProperty.Kill -> BlockStatsStorage.Request.SortBy.Kill
        BlockProperty.KillPerAttempt -> BlockStatsStorage.Request.SortBy.KillPerAttempt
        BlockProperty.Rebound -> BlockStatsStorage.Request.SortBy.Rebound
        BlockProperty.ReboundPerAttempt -> BlockStatsStorage.Request.SortBy.ReboundPerAttempt
        BlockProperty.KillPlusRebound -> BlockStatsStorage.Request.SortBy.KillPlusRebound
        BlockProperty.KillPlusReboundPerAttempt -> BlockStatsStorage.Request.SortBy.KillPlusReboundPerAttempt
        BlockProperty.Error -> BlockStatsStorage.Request.SortBy.Error
        BlockProperty.ErrorPerAttempt -> BlockStatsStorage.Request.SortBy.ErrorPerAttempt
        BlockProperty.PointWinPercent -> BlockStatsStorage.Request.SortBy.PointWinPercent
    }

private fun DigProperty.toSortBy(): DigStatsStorage.Request.SortBy =
    when (this) {
        DigProperty.Index, DigProperty.Name, DigProperty.TeamName,
        DigProperty.Specialization -> propertyError(this)
        DigProperty.Attempts -> DigStatsStorage.Request.SortBy.Attempts
        DigProperty.Digs -> DigStatsStorage.Request.SortBy.Digs
        DigProperty.SuccessPercent -> DigStatsStorage.Request.SortBy.SuccessPercent
        DigProperty.Errors -> DigStatsStorage.Request.SortBy.Errors
        DigProperty.PointWinPercent -> DigStatsStorage.Request.SortBy.PointWinPercent
    }

private fun ReceiveProperty.toSortBy(): ReceiveStatsStorage.Request.SortBy =
    when (this) {
        ReceiveProperty.Index, ReceiveProperty.Name, ReceiveProperty.TeamName,
        ReceiveProperty.Specialization -> propertyError(this)
        ReceiveProperty.Attempts -> ReceiveStatsStorage.Request.SortBy.Attempts
        ReceiveProperty.Perfect -> ReceiveStatsStorage.Request.SortBy.Perfect
        ReceiveProperty.PerfectPositive -> ReceiveStatsStorage.Request.SortBy.PerfectPositive
        ReceiveProperty.Efficiency -> ReceiveStatsStorage.Request.SortBy.Efficiency
        ReceiveProperty.Errors -> ReceiveStatsStorage.Request.SortBy.Errors
        ReceiveProperty.ErrorsPercent -> ReceiveStatsStorage.Request.SortBy.ErrorsPercent
        ReceiveProperty.SideOut -> ReceiveStatsStorage.Request.SortBy.SideOut
        ReceiveProperty.PointWinPercent -> ReceiveStatsStorage.Request.SortBy.PointWinPercent
    }

private fun ServeProperty.toSortBy(): ServeStatsStorage.Request.SortBy =
    when (this) {
        ServeProperty.Index, ServeProperty.Name, ServeProperty.TeamName,
        ServeProperty.Specialization -> propertyError(this)
        ServeProperty.Attempts -> ServeStatsStorage.Request.SortBy.Attempts
        ServeProperty.Efficiency -> ServeStatsStorage.Request.SortBy.Efficiency
        ServeProperty.Ace -> ServeStatsStorage.Request.SortBy.Ace
        ServeProperty.AcePercent -> ServeStatsStorage.Request.SortBy.AcePercent
        ServeProperty.Freeball -> ServeStatsStorage.Request.SortBy.Freeball
        ServeProperty.FreeballPercent -> ServeStatsStorage.Request.SortBy.FreeballPercent
        ServeProperty.AceFreeball -> ServeStatsStorage.Request.SortBy.AceFreeball
        ServeProperty.AceFreeballPercent -> ServeStatsStorage.Request.SortBy.AceFreeballPercent
        ServeProperty.Errors -> ServeStatsStorage.Request.SortBy.Errors
        ServeProperty.ErrorsPercent -> ServeStatsStorage.Request.SortBy.ErrorsPercent
        ServeProperty.PointWinPercent -> ServeStatsStorage.Request.SortBy.PointWinPercent
    }

private fun SetProperty.toSortBy(): SetStatsStorage.Request.SortBy =
    when (this) {
        SetProperty.Index, SetProperty.Name, SetProperty.TeamName,
        SetProperty.Specialization -> propertyError(this)
        SetProperty.Attempts -> SetStatsStorage.Request.SortBy.Attempts
        SetProperty.Perfect -> SetStatsStorage.Request.SortBy.Perfect
        SetProperty.PerfectPositive -> SetStatsStorage.Request.SortBy.PerfectPositive
        SetProperty.Efficiency -> SetStatsStorage.Request.SortBy.Efficiency
        SetProperty.Errors -> SetStatsStorage.Request.SortBy.Errors
        SetProperty.ErrorsPercent -> SetStatsStorage.Request.SortBy.ErrorsPercent
        SetProperty.PointWinPercent -> SetStatsStorage.Request.SortBy.PointWinPercent
    }

private fun propertyError(property: Property<String>): Nothing {
    error("Cannot sort by: ${property.id}")
}

private fun StatsModel.toRow(index: Int, properties: List<Property<String>>): DataRow =
    TableRow(cells = properties.map { toValueCell(index, it) })

private fun String.toDataCell(size: CellSize = CellSize.Medium): DataCell =
    DataCell(content = this, size = size)

private fun Property<String>.toHeaderCell(
    size: CellSize = CellSize.Medium,
    selectedProperty: Property<String>,
    onClicked: (Property<String>) -> Unit,
): HeaderCell = HeaderCell(
    firstLine = shortName,
    secondLine = additionalName,
    size = size,
    selected = this.id == selectedProperty.id,
    onClick = if (filterable) { { onClicked(this) } } else null
)

private fun StatsModel.toValueCell(index: Int, property: Property<String>): DataCell =
    when (this) {
        is AttackStatsStorage.Model -> AttackProperty.values().first { it.id == property.id }.toValueCell(index, this)
        is BlockStatsStorage.Model -> BlockProperty.values().first { it.id == property.id }.toValueCell(index, this)
        is ReceiveStatsStorage.Model -> ReceiveProperty.values().first { it.id == property.id }.toValueCell(index, this)
        is DigStatsStorage.Model -> DigProperty.values().first { it.id == property.id }.toValueCell(index, this)
        is SetStatsStorage.Model -> SetProperty.values().first { it.id == property.id }.toValueCell(index, this)
        is ServeStatsStorage.Model -> ServeProperty.values().first { it.id == property.id }.toValueCell(index, this)
    }

private fun AttackProperty.toValueCell(index: Int, model: AttackStatsStorage.Model): DataCell =
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

private fun BlockProperty.toValueCell(index: Int, model: BlockStatsStorage.Model): DataCell =
    when (this) {
        BlockProperty.Index -> (index + 1).toString().toDataCell(size = CellSize.Small)
        BlockProperty.Name -> model.name.shorterName().toDataCell()
        BlockProperty.TeamName -> model.teamName.toDataCell()
        BlockProperty.Specialization -> model.specialization.shortName.toDataCell()
        BlockProperty.Attempts -> model.attempts.toString().toDataCell()
        BlockProperty.Kill -> model.kill.toString().toDataCell()
        BlockProperty.KillPerAttempt -> model.killPerAttempt.toString().toDataCell()
        BlockProperty.Rebound -> model.rebound.toString().toDataCell()
        BlockProperty.ReboundPerAttempt -> model.reboundPerAttempt.toString().toDataCell()
        BlockProperty.KillPlusRebound -> model.killPlusRebound.toString().toDataCell()
        BlockProperty.KillPlusReboundPerAttempt -> model.killPlusReboundPerAttempt.toString().toDataCell()
        BlockProperty.Error -> model.error.toString().toDataCell()
        BlockProperty.ErrorPerAttempt -> model.errorPerAttempt.toString().toDataCell()
        BlockProperty.PointWinPercent -> model.pointWinPercent.toString().toDataCell()
    }

private fun DigProperty.toValueCell(index: Int, model: DigStatsStorage.Model): DataCell =
    when (this) {
        DigProperty.Index -> (index + 1).toString().toDataCell(size = CellSize.Small)
        DigProperty.Name -> model.name.shorterName().toDataCell()
        DigProperty.TeamName -> model.teamName.toDataCell()
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
        SetProperty.Specialization -> model.specialization.shortName.toDataCell()
        SetProperty.Attempts -> model.attempts.toString().toDataCell()
        SetProperty.Perfect -> model.perfect.toString().toDataCell()
        SetProperty.PerfectPositive -> model.perfectPositive.toString().toDataCell()
        SetProperty.Efficiency -> model.efficiency.toString().toDataCell()
        SetProperty.Errors -> model.errors.toString().toDataCell()
        SetProperty.ErrorsPercent -> model.errorsPercent.toString().toDataCell()
        SetProperty.PointWinPercent -> model.pointWinPercent.toString().toDataCell()
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

private fun StatsSkill.getDefaultSort(): Property<String> =
    when (this) {
        StatsSkill.Attack -> AttackProperty.Efficiency
        StatsSkill.Block -> BlockProperty.Kill
        StatsSkill.Dig -> DigProperty.Digs
        StatsSkill.Set -> SetProperty.Perfect
        StatsSkill.Receive -> ReceiveProperty.PerfectPositive
        StatsSkill.Serve -> ServeProperty.Ace
    }

private fun StatsSkill.allProperties(selectedProperties: List<String>): List<Property<String>> =
    allProperties.filter { selectedProperties.contains(it.id) }
