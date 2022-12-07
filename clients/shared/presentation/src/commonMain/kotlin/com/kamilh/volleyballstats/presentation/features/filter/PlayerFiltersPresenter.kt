package com.kamilh.volleyballstats.presentation.features.filter

import com.kamilh.volleyballstats.domain.models.*
import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.presentation.extensions.allProperties
import com.kamilh.volleyballstats.presentation.features.Presenter
import com.kamilh.volleyballstats.presentation.features.SavableMap
import com.kamilh.volleyballstats.presentation.features.TopBarState
import com.kamilh.volleyballstats.presentation.features.common.*
import com.kamilh.volleyballstats.presentation.navigation.NavigationEvent
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventSender
import com.kamilh.volleyballstats.storage.TeamStorage
import com.kamilh.volleyballstats.storage.TourStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

class PlayerFiltersPresenter private constructor(
    private val skill: StatsSkill = StatsSkill.Attack,
    private val teamStorage: TeamStorage,
    private val tourStorage: TourStorage,
    private val playerFiltersStorage: PlayerFiltersStorage,
    private val coroutineScope: CoroutineScope,
    private val navigationEventSender: NavigationEventSender,
) : Presenter {

    private val _state: MutableStateFlow<PlayerFiltersState> = MutableStateFlow(PlayerFilters().toPlayerFiltersState())
    val state: StateFlow<PlayerFiltersState> = _state.asStateFlow()

    init {
        playerFiltersStorage
            .getPlayerFilters(skill)
            .flatMapLatest { it.selectedSeasons.toFiltersStateFlow() }
            .onEach { _state.value = it }
            .launchIn(coroutineScope)
    }

    private suspend fun List<Season>.toFiltersStateFlow(): Flow<PlayerFiltersState> =
        combine(
            playerFiltersStorage.getPlayerFilters(skill),
            teamStorage.getTeamSnapshots(seasons = this),
            tourStorage.getAll(),
        ) { filters: PlayerFilters, teamSnapshots: Set<TeamSnapshot>, tours: List<Tour> ->
            filters.toPlayerFiltersState(
                allSeasons = tours.map { tour -> tour.season },
                allTeams = teamSnapshots,
            )
        }

    private fun PlayerFilters.toPlayerFiltersState(
        allProperties: List<Property<String>> = skill.allProperties,
        allSeasons: List<Season> = emptyList(),
        allSpecializations: List<Specialization> = Specialization.values().toList(),
        allTeams: Set<TeamSnapshot> = emptySet(),
        maxLimit: Int = 100,
    ): PlayerFiltersState = PlayerFiltersState(
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
        )
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
        playerFiltersStorage.toggleProperty(skill, id)
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
        playerFiltersStorage.toggleSeason(skill, value)
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
        playerFiltersStorage.toggleSpecialization(skill, value)
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
        playerFiltersStorage.toggleTeam(skill, value)
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
        playerFiltersStorage.setNewLimit(skill, value)
    }

    private fun onBackButtonClicked() {
        navigationEventSender.send(NavigationEvent.Close)
    }

    @Inject
    class Factory(
        private val teamStorage: TeamStorage,
        private val tourStorage: TourStorage,
        private val playerFiltersStorage: PlayerFiltersStorage,
        private val navigationEventSender: NavigationEventSender,
    ) : Presenter.Factory<PlayerFiltersPresenter, StatsSkill> {

        override fun create(
            coroutineScope: CoroutineScope,
            savableMap: SavableMap,
            extras: StatsSkill,
        ): PlayerFiltersPresenter = PlayerFiltersPresenter(
            skill = extras,
            teamStorage = teamStorage,
            tourStorage = tourStorage,
            playerFiltersStorage = playerFiltersStorage,
            coroutineScope = coroutineScope,
            navigationEventSender = navigationEventSender,
        )
    }
}
