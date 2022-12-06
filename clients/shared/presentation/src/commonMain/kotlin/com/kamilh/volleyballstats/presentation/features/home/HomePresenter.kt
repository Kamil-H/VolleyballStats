package com.kamilh.volleyballstats.presentation.features.home

import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.interactors.SynchronizeState
import com.kamilh.volleyballstats.interactors.SynchronizeStateReceiver
import com.kamilh.volleyballstats.interactors.Synchronizer
import com.kamilh.volleyballstats.presentation.features.Presenter
import com.kamilh.volleyballstats.presentation.features.SavableMap
import com.kamilh.volleyballstats.presentation.features.TopBarState
import com.kamilh.volleyballstats.presentation.features.common.*
import com.kamilh.volleyballstats.presentation.navigation.NavigationEventSender
import com.kamilh.volleyballstats.storage.TourStorage
import com.kamilh.volleyballstats.storage.match.MatchSnapshotStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

class HomePresenter private constructor(
    private val coroutineScope: CoroutineScope,
    private val matchSnapshotStorage: MatchSnapshotStorage,
    private val navigationEventSender: NavigationEventSender,
    private val synchronizeStateReceiver: SynchronizeStateReceiver,
    private val synchronizer: Synchronizer,
    private val tourStorage: TourStorage,
) : Presenter {

    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(
        HomeState(
            onRefreshButtonClicked = ::refresh,
            onScrolledToItem = ::onScrolledToItem,
            topBarState = TopBarState(
                title = "Matches",
                showToolbar = true,
                actionButtonIcon = Icon.Refresh,
            )
        )
    )
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        tourStorage.getLatestSeason()
            .flatMapLatest(::getMatches)
            .combine(synchronizeStateReceiver.receive(), ::updateState)
            .launchIn(coroutineScope)
    }

    private fun getMatches(season: Season?): Flow<List<MatchSnapshotStorage.Model>> =
        if (season == null) {
            flowOf(emptyList())
        } else {
            matchSnapshotStorage.getMatches(season).distinctUntilChanged()
        }

    private fun updateState(matchSnapshots: List<MatchSnapshotStorage.Model>, synchronizeState: SynchronizeState) {
        val groupedItems = matchSnapshots.groupBy {
            it.date?.toLocalDate()
        }.map { (localDate, matches) ->
            GroupedMatchItem(
                title = localDate?.toIso8601String() ?: "Unspecified date",
                items = matches.map { it.toMatchState() }
            )
        }
        _state.update { currentState ->
            val loadingState = synchronizeState.toLoadingState(hasContent = matchSnapshots.isNotEmpty())
            val closestHeaderIndex = closestHeaderIndex(matchSnapshots)
            val scrollToItem = if (groupedItems.isNotEmpty() && groupedItems != currentState.matches) {
                closestHeaderIndex
            } else currentState.scrollToItem
            currentState.copy(
                matches = groupedItems,
                scrollToItem = scrollToItem,
                loadingState = loadingState,
                itemToSnapTo = closestHeaderIndex ?: 0,
            )
        }
    }

    private fun MatchSnapshotStorage.Model.toMatchState(): MatchItem =
        MatchItem(
            id = id.value,
            left = MatchItem.SideDetails(
                label = home.name,
                imageUrl = home.logo,
            ),
            right = MatchItem.SideDetails(
                label = away.name,
                imageUrl = away.logo,
            ),
            centerText = centerText(),
            bottomText = mvpName?.let { TextPair(first = "MVP", second = it) },
        )

    private fun MatchSnapshotStorage.Model.centerText(): String {
        val homeResult = home.result
        val awayResult = away.result
        val time = date?.timeString()
        return if (homeResult != null && awayResult != null) {
            "$homeResult - $awayResult"
        } else time ?: "00:00"
    }

    private fun refresh() {
        synchronizer.synchronize(League.POLISH_LEAGUE)
    }

    private fun onScrolledToItem(itemIndex: Int) {
        _state.update { currentState ->
            if (currentState.scrollToItem == itemIndex) {
                currentState.copy(scrollToItem = null)
            } else currentState
        }
    }

    private fun closestHeaderIndex(models: List<MatchSnapshotStorage.Model>): Int? {
        val today = CurrentDate.localDate.atMidnight()
        val dates = models.mapNotNull { it.date?.atMidnight() }
        val betweenDays = dates.mapIndexed { index, dateTime ->
            index to dateTime.between(today)
        }
        val matchDays = dates.map { it }.toSet()
        if (matchDays.isEmpty()) return null
        val indexOfMinDistance = betweenDays.minBy { it.second }.first
        val dateOfMin = models[indexOfMinDistance].date?.atMidnight()
        val daysToAdd = matchDays.indexOfFirst { it == dateOfMin }
        return indexOfMinDistance + daysToAdd
    }

    @Inject
    class Factory(
        private val matchSnapshotStorage: MatchSnapshotStorage,
        private val navigationEventSender: NavigationEventSender,
        private val synchronizeStateReceiver: SynchronizeStateReceiver,
        private val synchronizer: Synchronizer,
        private val tourStorage: TourStorage,
    ) : Presenter.Factory<HomePresenter, Unit> {

        override fun create(
            coroutineScope: CoroutineScope,
            savableMap: SavableMap,
            extras: Unit,
        ): HomePresenter = HomePresenter(
            coroutineScope = coroutineScope,
            matchSnapshotStorage = matchSnapshotStorage,
            navigationEventSender = navigationEventSender,
            synchronizeStateReceiver = synchronizeStateReceiver,
            synchronizer = synchronizer,
            tourStorage = tourStorage,
        )
    }
}

