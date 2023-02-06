package com.kamilh.volleyballstats.presentation.features.players

import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.interactors.SynchronizeStateReceiver
import com.kamilh.volleyballstats.presentation.extensions.allProperties
import com.kamilh.volleyballstats.presentation.features.ColorAccent
import com.kamilh.volleyballstats.presentation.features.Presenter
import com.kamilh.volleyballstats.presentation.features.SavableMap
import com.kamilh.volleyballstats.presentation.features.TopBarState
import com.kamilh.volleyballstats.presentation.features.common.*
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
    private val statsType: StatsType,
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
            topBarState = TopBarState(background = TopBarState.Color.Primary),
            colorAccent = when (statsType) {
                StatsType.Player -> ColorAccent.Primary
                StatsType.Team -> ColorAccent.Tertiary
            },
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
                    val loadingState = synchronizeState.toLoadingState(hasContent = tableContent.rows.isNotEmpty())
                    currentState.copy(
                        tableContent = tableContent,
                        loadingState = loadingState,
                        actionButton = currentState.actionButton.copy(show = !synchronizeState.isLoading),
                    )
                }
            }
            .launchIn(coroutineScope)
    }

    private fun PlayerFiltersStorage.request(): Flow<Pair<StatsRequest, List<Property<String>>>> =
        chosenSkill.flatMapLatest { skill ->
            getPlayerFilters(skill, statsType = statsType).combine(sortBy) { playerFilters, sortBy ->
                statsFlowFactory.createRequest(
                    playerFilters = playerFilters,
                    skill = skill,
                    sortBy = sortBy[skill]!!,
                    statsType = statsType,
                ) to skill.allProperties(statsType, playerFilters.selectedProperties)
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
        navigationEventSender.send(NavigationEvent.PlayerFiltersRequested(chosenSkill.value, statsType))
    }

    @Inject
    class Factory(
        private val statsModelMapper: StatsModelMapper,
        private val statsFlowFactory: StatsFlowFactory,
        private val playerFiltersStorage: PlayerFiltersStorage,
        private val navigationEventSender: NavigationEventSender,
        private val synchronizeStateReceiver: SynchronizeStateReceiver,
    ) : Presenter.Factory<PlayerStatsPresenter, StatsType> {

        override fun create(
            coroutineScope: CoroutineScope,
            savableMap: SavableMap,
            extras: StatsType,
        ): PlayerStatsPresenter = PlayerStatsPresenter(
            statsType = extras,
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

private fun StatsSkill.allProperties(
    type: StatsType,
    selectedProperties: List<String>,
): List<Property<String>> = allProperties(type).filter { selectedProperties.contains(it.id) }
