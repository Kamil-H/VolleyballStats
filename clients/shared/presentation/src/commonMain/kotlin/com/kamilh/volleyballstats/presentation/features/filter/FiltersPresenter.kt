package com.kamilh.volleyballstats.presentation.features.filter

import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.Specialization
import com.kamilh.volleyballstats.domain.models.StatsFilters
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.TeamSnapshot
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.presentation.extensions.allProperties
import com.kamilh.volleyballstats.presentation.features.ColorAccent
import com.kamilh.volleyballstats.presentation.features.Presenter
import com.kamilh.volleyballstats.presentation.features.SavableMap
import com.kamilh.volleyballstats.presentation.features.TopBarState
import com.kamilh.volleyballstats.presentation.features.common.CheckableProperty
import com.kamilh.volleyballstats.presentation.features.common.ChoosePropertiesState
import com.kamilh.volleyballstats.presentation.features.common.ChooseValueState
import com.kamilh.volleyballstats.presentation.features.common.Icon
import com.kamilh.volleyballstats.presentation.features.common.Property
import com.kamilh.volleyballstats.presentation.features.common.SegmentedControlState
import com.kamilh.volleyballstats.presentation.features.common.SelectOptionState
import com.kamilh.volleyballstats.presentation.features.common.check
import com.kamilh.volleyballstats.presentation.features.common.select
import com.kamilh.volleyballstats.presentation.features.common.setNewValue
import com.kamilh.volleyballstats.presentation.navigation.NavigationEvent
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventSender
import com.kamilh.volleyballstats.storage.TeamStorage
import com.kamilh.volleyballstats.storage.TourStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class FiltersPresenter private constructor(
    private val args: Args,
    private val teamStorage: TeamStorage,
    private val tourStorage: TourStorage,
    private val statsFiltersStorage: StatsFiltersStorage,
    private val coroutineScope: CoroutineScope,
    private val navigationEventSender: NavigationEventSender,
) : Presenter {

    private val selectedControlIndex: MutableStateFlow<Int> = MutableStateFlow(value = 0)
    private val _state: MutableStateFlow<FiltersState> = MutableStateFlow(
        StatsFilters().toFiltersState(selectedControlIndex = selectedControlIndex.value))
    val state: StateFlow<FiltersState> = _state.asStateFlow()

    init {
        statsFiltersStorage
            .getStatsFilters(args.skill, args.type)
            .flatMapLatest { it.selectedSeasons.toFiltersStateFlow() }
            .onEach { _state.value = it }
            .launchIn(coroutineScope)
    }

    private suspend fun List<Season>.toFiltersStateFlow(): Flow<FiltersState> =
        combine(
            statsFiltersStorage.getStatsFilters(args.skill, args.type),
            teamStorage.getTeamSnapshots(seasons = this),
            tourStorage.getAll(),
            selectedControlIndex,
        ) { filters: StatsFilters, teamSnapshots: Set<TeamSnapshot>, tours: List<Tour>, selectedControlIndex ->
            filters.toFiltersState(
                allSeasons = tours.map { tour -> tour.season },
                allTeams = teamSnapshots,
                selectedControlIndex = selectedControlIndex,
            )
        }

    private fun StatsFilters.toFiltersState(
        allProperties: List<Property<String>> = args.skill.allProperties(args.type),
        allSeasons: List<Season> = emptyList(),
        allSpecializations: List<Specialization> = Specialization.values().toList(),
        allTeams: Set<TeamSnapshot> = emptySet(),
        maxLimit: Int = 100,
        selectedControlIndex: Int,
    ): FiltersState = FiltersState(
        properties = selectedProperties.toChoosePropertiesState(allProperties),
        seasonSelectOption = selectedSeasons.toSeasonOptionState(allSeasons),
        specializationSelectOption = selectedSpecializations.toSpecializationOptionState(allSpecializations),
        teamsSelectOption = selectedTeams.toTeamOptionState(allTeams),
        chooseIntState = selectedLimit.toChooseIntState(maxLimit),
        onBackButtonClicked = ::onBackButtonClicked,
        topBarState = TopBarState(
            title = "Adjust",
            navigationButtonIcon = Icon.ArrowBack,
            showToolbar = true,
        ),
        segmentedControlState = SegmentedControlState(
            items = Control.values().map { it.itemName },
            selectedIndex = selectedControlIndex,
        ),
        showControl = Control.values()[selectedControlIndex],
        colorAccent = when (args.type) {
            StatsType.Player -> ColorAccent.Primary
            StatsType.Team -> ColorAccent.Tertiary
        },
    )

    // PROPERTIES

    private fun List<String>.toChoosePropertiesState(allProperties: List<Property<String>>): ChoosePropertiesState<String> =
        ChoosePropertiesState(
            title = "Choose properties",
            checkableProperties = allProperties.map {
                it.toCheckableProperty(selectedProperties = this)
            },
            onChecked = ::onPropertyChecked
        )

    private fun Property<String>.toCheckableProperty(selectedProperties: List<String>): CheckableProperty<String> =
        CheckableProperty(
            checked = selectedProperties.find { it == this.id } != null,
            property = this,
        )

    private fun onPropertyChecked(id: String) {
        _state.update { it.copy(properties = it.properties.check(id)) }
        statsFiltersStorage.toggleProperty(args.skill, args.type, id)
    }

    // SEASON

    private fun List<Season>.toSeasonOptionState(allValues: List<Season>): SelectOptionState<Season> =
        SelectOptionState(
            title = "Choose seasons",
            options = allValues.map { it.toOption(this) },
            onSelected = ::onSelected,
        )

    private fun Season.toOption(selectedValues: List<Season>): SelectOptionState.Option<Season> =
        SelectOptionState.Option(
            id = this,
            label = value.toString(),
            selected = selectedValues.contains(this),
        )

    private fun onSelected(value: Season) {
        _state.update { it.copy(seasonSelectOption = it.seasonSelectOption.select(value)) }
        statsFiltersStorage.toggleSeason(args.skill, args.type, value)
    }

    // SPECIALIZATION

    private fun List<Specialization>.toSpecializationOptionState(allValues: List<Specialization>): SelectOptionState<Specialization> =
        SelectOptionState(
            title = "Choose specializations",
            options = allValues.map { it.toOption(this) },
            onSelected = ::onSelected,
        )

    private fun Specialization.toOption(selectedValues: List<Specialization>): SelectOptionState.Option<Specialization> =
        SelectOptionState.Option(
            id = this,
            label = name, // TODO: change to correct name
            selected = selectedValues.contains(this),
        )

    private fun onSelected(value: Specialization) {
        _state.update { it.copy(specializationSelectOption = it.specializationSelectOption.select(value)) }
        statsFiltersStorage.toggleSpecialization(args.skill, args.type, value)
    }

    // TEAMS

    private fun List<TeamId>.toTeamOptionState(allValues: Set<TeamSnapshot>): SelectOptionState<TeamId> =
        SelectOptionState(
            title = "Choose teams",
            options = allValues.map { it.toOption(this) },
            onSelected = ::onSelected,
        )

    private fun TeamSnapshot.toOption(selectedValues: List<TeamId>): SelectOptionState.Option<TeamId> =
        SelectOptionState.Option(
            id = teamId,
            label = code,
            selected = selectedValues.contains(this.teamId),
        )

    private fun onSelected(value: TeamId) {
        _state.update { it.copy(teamsSelectOption = it.teamsSelectOption.select(value)) }
        statsFiltersStorage.toggleTeam(args.skill, args.type, value)
    }

    // LIMIT

    private fun Int.toChooseIntState(maxLimit: Int): ChooseValueState<Int> =
        ChooseValueState(
            title = "Choose limit",
            value = this,
            maxValue = maxLimit,
            onValueSelected = ::onValueSelected,
        )

    private fun onValueSelected(value: Int) {
        _state.update { it.copy(chooseIntState = it.chooseIntState.setNewValue(value)) }
        statsFiltersStorage.setNewLimit(args.skill, args.type, value)
    }

    private fun onBackButtonClicked() {
        navigationEventSender.send(NavigationEvent.Close)
    }

    fun onControlItemSelected(selectedIndex: Int) {
        selectedControlIndex.value = selectedIndex
    }

    @Inject
    class Factory(
        private val teamStorage: TeamStorage,
        private val tourStorage: TourStorage,
        private val statsFiltersStorage: StatsFiltersStorage,
        private val navigationEventSender: NavigationEventSender,
    ) : Presenter.Factory<FiltersPresenter, Args> {

        override fun create(
            coroutineScope: CoroutineScope,
            savableMap: SavableMap,
            extras: Args,
        ): FiltersPresenter = FiltersPresenter(
            args = extras,
            teamStorage = teamStorage,
            tourStorage = tourStorage,
            statsFiltersStorage = statsFiltersStorage,
            coroutineScope = coroutineScope,
            navigationEventSender = navigationEventSender,
        )
    }

    data class Args(
        val skill: StatsSkill,
        val type: StatsType,
    )
}
