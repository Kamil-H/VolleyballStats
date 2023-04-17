package com.kamilh.volleyballstats.presentation.features.stats

import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.interactors.SynchronizeStateReceiver
import com.kamilh.volleyballstats.presentation.extensions.allProperties
import com.kamilh.volleyballstats.presentation.features.ColorAccent
import com.kamilh.volleyballstats.presentation.features.Presenter
import com.kamilh.volleyballstats.presentation.features.SavableMap
import com.kamilh.volleyballstats.presentation.features.TopBarState
import com.kamilh.volleyballstats.presentation.features.common.Property
import com.kamilh.volleyballstats.presentation.features.common.SelectOptionState
import com.kamilh.volleyballstats.presentation.features.common.TableContent
import com.kamilh.volleyballstats.presentation.features.common.isLoading
import com.kamilh.volleyballstats.presentation.features.common.selectSingle
import com.kamilh.volleyballstats.presentation.features.common.toLoadingState
import com.kamilh.volleyballstats.presentation.features.filter.StatsFiltersStorage
import com.kamilh.volleyballstats.presentation.features.stats.properties.AttackProperty
import com.kamilh.volleyballstats.presentation.features.stats.properties.BlockProperty
import com.kamilh.volleyballstats.presentation.features.stats.properties.DigProperty
import com.kamilh.volleyballstats.presentation.features.stats.properties.ReceiveProperty
import com.kamilh.volleyballstats.presentation.features.stats.properties.ServeProperty
import com.kamilh.volleyballstats.presentation.features.stats.properties.SetProperty
import com.kamilh.volleyballstats.presentation.navigation.NavigationEvent
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventSender
import com.kamilh.volleyballstats.storage.stats.StatsModel
import com.kamilh.volleyballstats.storage.stats.StatsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class StatsPresenter private constructor(
    private val statsType: StatsType,
    private val statsModelMapper: StatsModelMapper,
    private val statsFlowFactory: StatsFlowFactory,
    private val coroutineScope: CoroutineScope,
    private val statsFiltersStorage: StatsFiltersStorage,
    private val navigationEventSender: NavigationEventSender,
    private val synchronizeStateReceiver: SynchronizeStateReceiver,
) : Presenter {

    private val chosenSkill = MutableStateFlow(StatsSkill.Attack)
    private val _state: MutableStateFlow<StatsState> = MutableStateFlow(
        StatsState(
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
    val state: StateFlow<StatsState> = _state.asStateFlow()

    private val sortBy: MutableStateFlow<Map<StatsSkill, Property<String>>> = MutableStateFlow(
        StatsSkill.values().associateWith { it.getDefaultSort() }
    )

    init {
        val sortByProperty = sortBy.combine(chosenSkill) { sortBy, skill -> sortBy[skill]!! }
        statsFiltersStorage
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

    private fun StatsFiltersStorage.request(): Flow<Pair<StatsRequest, List<Property<String>>>> =
        chosenSkill.flatMapLatest { skill ->
            getStatsFilters(skill, statsType = statsType).combine(sortBy) { statsFilters, sortBy ->
                statsFlowFactory.createRequest(
                    statsFilters = statsFilters,
                    skill = skill,
                    sortBy = sortBy[skill]!!,
                    statsType = statsType,
                ) to skill.allProperties(statsType, statsFilters.selectedProperties)
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
        private val statsFiltersStorage: StatsFiltersStorage,
        private val navigationEventSender: NavigationEventSender,
        private val synchronizeStateReceiver: SynchronizeStateReceiver,
    ) : Presenter.Factory<StatsPresenter, StatsType> {

        override fun create(
            coroutineScope: CoroutineScope,
            savableMap: SavableMap,
            extras: StatsType,
        ): StatsPresenter = StatsPresenter(
            statsType = extras,
            statsModelMapper = statsModelMapper,
            statsFlowFactory = statsFlowFactory,
            coroutineScope = coroutineScope,
            statsFiltersStorage = statsFiltersStorage,
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
