package com.kamilh.volleyballstats.presentation.features.players

import com.kamilh.volleyballstats.domain.models.PlayerFilters
import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.presentation.extensions.findProperty
import com.kamilh.volleyballstats.presentation.features.common.Property
import com.kamilh.volleyballstats.presentation.features.players.properties.*
import com.kamilh.volleyballstats.storage.stats.*
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface StatsFlowFactory {

    fun createRequest(playerFilters: PlayerFilters, skill: StatsSkill, sortBy: Property<String>): StatsRequest

    fun resolve(statsRequest: StatsRequest): Flow<List<StatsModel>>
}

@Inject
class StatsFlowFactoryImpl(
    private val attackStatsStorage: AttackStatsStorage,
    private val blockStatsStorage: BlockStatsStorage,
    private val digStatsStorage: DigStatsStorage,
    private val receiveStatsStorage: ReceiveStatsStorage,
    private val serveStatsStorage: ServeStatsStorage,
    private val setStatsStorage: SetStatsStorage,
) : StatsFlowFactory {

    override fun createRequest(
        playerFilters: PlayerFilters,
        skill: StatsSkill,
        sortBy: Property<String>,
    ): StatsRequest = playerFilters.toRequest(skill, sortBy)

    override fun resolve(statsRequest: StatsRequest): Flow<List<StatsModel>> =
        statsRequest.toModelFlow()

    private fun StatsRequest.toModelFlow(): Flow<List<StatsModel>> =
        when (this) {
            is AttackStatsStorage.Request -> attackStatsStorage.getStats(this)
            is BlockStatsStorage.Request -> blockStatsStorage.getStats(this)
            is DigStatsStorage.Request -> digStatsStorage.getStats(this)
            is ReceiveStatsStorage.Request -> receiveStatsStorage.getStats(this)
            is ServeStatsStorage.Request -> serveStatsStorage.getStats(this)
            is SetStatsStorage.Request -> setStatsStorage.getStats(this)
        }
}

@Suppress("LongMethod")
private fun PlayerFilters.toRequest(skill: StatsSkill, sortBy: Property<String>): StatsRequest =
    when (skill) {
        StatsSkill.Attack -> AttackStatsStorage.Request(
            groupBy = StatsRequest.GroupBy.Player,
            sortBy = findProperty<AttackProperty>(sortBy).toSortBy(),
            seasons = selectedSeasons,
            specializations = selectedSpecializations,
            teams = selectedTeams,
            minAttempts = selectedLimit.toLong(),
        )
        StatsSkill.Block -> BlockStatsStorage.Request(
            groupBy = StatsRequest.GroupBy.Player,
            sortBy = findProperty<BlockProperty>(sortBy).toSortBy(),
            seasons = selectedSeasons,
            specializations = selectedSpecializations,
            teams = selectedTeams,
            minAttempts = selectedLimit.toLong(),
        )
        StatsSkill.Dig -> DigStatsStorage.Request(
            groupBy = StatsRequest.GroupBy.Player,
            sortBy = findProperty<DigProperty>(sortBy).toSortBy(),
            seasons = selectedSeasons,
            specializations = selectedSpecializations,
            teams = selectedTeams,
            minAttempts = selectedLimit.toLong(),
        )
        StatsSkill.Set -> SetStatsStorage.Request(
            groupBy = StatsRequest.GroupBy.Player,
            sortBy = findProperty<SetProperty>(sortBy).toSortBy(),
            seasons = selectedSeasons,
            specializations = selectedSpecializations,
            teams = selectedTeams,
            minAttempts = selectedLimit.toLong(),
        )
        StatsSkill.Receive -> ReceiveStatsStorage.Request(
            groupBy = StatsRequest.GroupBy.Player,
            sortBy = findProperty<ReceiveProperty>(sortBy).toSortBy(),
            seasons = selectedSeasons,
            specializations = selectedSpecializations,
            teams = selectedTeams,
            minAttempts = selectedLimit.toLong(),
        )
        StatsSkill.Serve -> ServeStatsStorage.Request(
            groupBy = StatsRequest.GroupBy.Player,
            sortBy = findProperty<ServeProperty>(sortBy).toSortBy(),
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
