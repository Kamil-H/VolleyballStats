package com.kamilh.volleyballstats.presentation.features.players

import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.interactors.SynchronizeState
import com.kamilh.volleyballstats.interactors.SynchronizeStateReceiver
import com.kamilh.volleyballstats.presentation.extensions.allProperties
import com.kamilh.volleyballstats.presentation.features.Presenter
import com.kamilh.volleyballstats.presentation.features.SavableMap
import com.kamilh.volleyballstats.presentation.features.common.Property
import com.kamilh.volleyballstats.presentation.features.common.SelectOptionState
import com.kamilh.volleyballstats.presentation.features.common.TableContent
import com.kamilh.volleyballstats.presentation.features.common.selectSingle
import com.kamilh.volleyballstats.presentation.features.filter.PlayerFiltersStorage
import com.kamilh.volleyballstats.presentation.features.players.properties.*
import com.kamilh.volleyballstats.presentation.navigation.NavigationEvent
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventSender
import com.kamilh.volleyballstats.storage.stats.StatsModel
import com.kamilh.volleyballstats.storage.stats.StatsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

class PlayerStatsPresenter private constructor(
    private val statsModelMapper: StatsModelMapper,
    private val statsFlowFactory: StatsFlowFactory,
    private val coroutineScope: CoroutineScope,
    private val playerFiltersStorage: PlayerFiltersStorage,
    private val navigationEventSender: NavigationEventSender,
    private val synchronizeStateReceiver: SynchronizeStateReceiver,
) : Presenter {

    private val chosenSkill = MutableStateFlow(StatsSkill.Attack)
    private val _state: MutableStateFlow<PlayerStatsState> = MutableStateFlow(
        PlayerStatsState(
            selectSkillState = SelectOptionState(options = StatsSkill.values().map { skill ->
                SelectOptionState.Option(
                    id = skill,
                    label = skill.name,
                    selected = skill == chosenSkill.value,
                )
            }, onSelected = ::onSkillClicked),
            onFabButtonClicked = ::onFabButtonClicked,
        )
    )
    val state: StateFlow<PlayerStatsState> = _state.asStateFlow()

    private val sortBy: MutableStateFlow<Map<StatsSkill, Property<String>>> = MutableStateFlow(
        StatsSkill.values().associateWith { it.getDefaultSort() }
    )

    init {
        val sortByProperty = sortBy.combine(chosenSkill) { sortBy, skill -> sortBy[skill]!! }
        playerFiltersStorage
            .request()
            .flatMapLatest { (statsRequest, properties)  ->
                statsFlowFactory.resolve(statsRequest).map { it to properties }
            }
            .combine(sortByProperty) { (stats, properties), sortBy ->
                produceNewState(stats, properties, sortBy)
            }
            .combine(synchronizeStateReceiver.receive()) { tableContent, synchronizeState ->
                _state.update { currentState ->
                    val loadingState = synchronizeState.toLoadingState()
                    val isLoading = loadingState != null
                    val hasContent = tableContent.rows.isNotEmpty()
                    currentState.copy(
                        tableContent = tableContent,
                        loadingState = loadingState,
                        showFullScreenLoading = isLoading && !hasContent,
                        showSmallLoading = isLoading && hasContent,
                        showFab = !isLoading,
                    )
                }
            }
            .launchIn(coroutineScope)
    }

    private fun PlayerFiltersStorage.request(): Flow<Pair<StatsRequest, List<Property<String>>>> =
        chosenSkill.flatMapLatest { skill ->
            getPlayerFilters(skill).combine(sortBy) { playerFilters, sortBy ->
                statsFlowFactory.createRequest(
                    playerFilters = playerFilters,
                    skill = skill,
                    sortBy = sortBy[skill]!!,
                ) to skill.allProperties(playerFilters.selectedProperties)
            }
        }

    private fun produceNewState(
        stats: List<StatsModel>,
        properties: List<Property<String>>,
        selectedProperty: Property<String>,
    ): TableContent = statsModelMapper.map(
        stats = stats,
        properties = properties,
        selectedProperty = selectedProperty,
        callback = ::onPropertySelected
    )

    private fun onPropertySelected(property: Property<String>) {
        sortBy.update { map ->
            map.toMutableMap().apply {
                this[chosenSkill.value] = property
            }
        }
    }

    private fun onSkillClicked(id: StatsSkill) {
        _state.update { state ->
            state.copy(selectSkillState = state.selectSkillState.selectSingle(id))
        }
        chosenSkill.value = id
    }

    private fun onFabButtonClicked() {
        navigationEventSender.send(NavigationEvent.PlayerFiltersRequested(chosenSkill.value))
    }

    private fun SynchronizeState.toLoadingState(): LoadingState? =
        when (this) {
            is SynchronizeState.Started -> "Updating..."
            is SynchronizeState.UpdatingMatches -> "Downloading ${this.matches.size} matches in ${tour.season.value} season"
            SynchronizeState.Error, SynchronizeState.Idle, SynchronizeState.Success -> null
        }?.let(::LoadingState)

    @Inject
    class Factory(
        private val statsModelMapper: StatsModelMapper,
        private val statsFlowFactory: StatsFlowFactory,
        private val playerFiltersStorage: PlayerFiltersStorage,
        private val navigationEventSender: NavigationEventSender,
        private val synchronizeStateReceiver: SynchronizeStateReceiver,
    ) : Presenter.Factory<PlayerStatsPresenter, Unit> {

        override fun create(
            coroutineScope: CoroutineScope,
            savableMap: SavableMap,
            extras: Unit,
        ): PlayerStatsPresenter = PlayerStatsPresenter(
            statsModelMapper = statsModelMapper,
            statsFlowFactory = statsFlowFactory,
            coroutineScope = coroutineScope,
            playerFiltersStorage = playerFiltersStorage,
            navigationEventSender = navigationEventSender,
            synchronizeStateReceiver = synchronizeStateReceiver,
        )
    }
}

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
