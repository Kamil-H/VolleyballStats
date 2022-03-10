package com.kamilh.storage

import com.kamilh.databse.SelectAllMatchesByTour
import com.kamilh.models.*
import com.kamilh.repository.polishleague.seasonOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.time.Duration

class SqlMatchStorageTest : StatisticsStorageTest() {

    private val matchStorage by lazy {
        SqlMatchStorage(
            queryRunner = testQueryRunner,
            matchQueries = matchQueries,
            tourQueries = tourQueries,
        )
    }

    private fun configure(
        season: Season = seasonOf(),
        league: League = leagueOf(),
    ): Tour {
        val tour = tourOf(season = season, league = league)
        insert(league)
        insert(tour)
        insert(
            InsertTeam(
                team = teamOf(),
                tour = tour,
            )
        )
        return tour
    }

    @Test
    fun `insert returns TourNotFound when there is no tour in the database`() = runBlocking {
        // GIVEN
        val tourId = tourIdOf()

        // WHEN
        val result = matchStorage.insertOrUpdate(emptyList(), tourId)

        // THEN
        result.assertFailure {
            assert(this == InsertMatchesError.TourNotFound)
        }
    }

    @Test
    fun `insert returns Success when there is a tour in the database`() = runBlocking {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = tourOf(season = season, league = league)
        insert(league)
        insert(tour)

        // WHEN
        val result = matchStorage.insertOrUpdate(emptyList(), tour.id)

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `insert select returns Saved when there is MatchReport associated with the Match`() = runBlocking {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = configure(season, league)
        val matchId = matchIdOf(1)
        val match = potentiallyFinishedOf(id = matchId)
        matchStorage.insertOrUpdate(listOf(match), tour.id)
        val matchStatistics = load(league = league, season = season, matchId = matchId)

        // WHEN
        val result = matchStorage.getAllMatches(tour.id).first().first()

        // THEN
        require(result is AllMatchesItem.Saved)
        val lastSet = matchStatistics.sets.last()
        assert(result.winnerId == if (lastSet.score.home > lastSet.score.away) matchStatistics.home.teamId else matchStatistics.away.teamId)
        assert(result.endTime == lastSet.endTime)
        assert(result.matchReportId == matchStatistics.matchReportId)
    }

    @Test
    fun `insert NotScheduled works properly`() = runBlocking {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = configure(season, league)
        val matchId = matchIdOf(2)
        val match = notScheduledOf(id = matchId)

        // WHEN
        matchStorage.insertOrUpdate(listOf(match), tour.id)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tour.id).executeAsOne()
        val expectedValue = selectAllMatchesByTourOf(id = matchId, state = MatchState.NotScheduled)
        assert(value == expectedValue)
    }

    @Test
    fun `insert PotentiallyFinished works properly`() = runBlocking {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = configure(season, league)
        val matchId = matchIdOf(2)
        val match = potentiallyFinishedOf(id = matchId)

        // WHEN
        matchStorage.insertOrUpdate(listOf(match), tour.id)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tour.id).executeAsOne()
        val expectedValue = selectAllMatchesByTourOf(id = matchId, state = MatchState.PotentiallyFinished)
        assert(value == expectedValue)
    }

    @Test
    fun `insert Scheduled works properly`() = runBlocking {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = configure(season, league)
        val matchId = matchIdOf(2)
        val date = OffsetDateTime.now(clock)
        val match = scheduledOf(id = matchId, date = date)

        // WHEN
        matchStorage.insertOrUpdate(listOf(match), tour.id)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tour.id).executeAsOne()
        val expectedValue = selectAllMatchesByTourOf(id = matchId, date = date, state = MatchState.Scheduled)
        assert(value == expectedValue)
    }

    @Test
    fun `insert Saved returns error`() = runBlocking {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = tourOf(season = season, league = league)
        insert(league)
        insert(tour)
        val matchId = matchIdOf(2)
        val match = savedOf(id = matchId)

        // WHEN
        val result = matchStorage.insertOrUpdate(listOf(match), tour.id)

        // THEN
        result.assertFailure {
            require(this is InsertMatchesError.TryingToInsertSavedItems)
            assert(this.saved == listOf(match))
        }
    }

    @Test
    fun `insert updates a value properly`() = runBlocking {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = configure(season, league)
        val matchId = matchIdOf(2)
        val date = OffsetDateTime.now(clock)
        val match = scheduledOf(id = matchId, date = date)

        // WHEN
        matchStorage.insertOrUpdate(listOf(match), tour.id)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tour.id).executeAsOne()
        val expectedValue = selectAllMatchesByTourOf(id = matchId, date = date, state = MatchState.Scheduled)
        assert(value == expectedValue)

        // WHEN
        val newDate = null
        val newMatch = potentiallyFinishedOf(id = matchId, date = newDate)
        matchStorage.insertOrUpdate(listOf(newMatch), tour.id)

        // THEN
        val newValue = matchQueries.selectAllMatchesByTour(tour.id).executeAsOne()
        val newExpectedValue = selectAllMatchesByTourOf(
            id = matchId,
            date = newDate,
            state = MatchState.PotentiallyFinished
        )
        assert(newValue == newExpectedValue)
    }

    @Test
    fun `insert updates a match that already has associated MatchReport value properly`() = runBlocking {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = configure()
        val matchId = matchIdOf(2)
        val match = potentiallyFinishedOf(id = matchId)
        val matchReportId = matchReportIdOf(1)

        // WHEN
        matchStorage.insertOrUpdate(listOf(match), tour.id)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tour.id).executeAsOne()
        val expectedValue = selectAllMatchesByTourOf(id = matchId, state = MatchState.PotentiallyFinished)
        assert(value == expectedValue)

        // WHEN
        matchStatisticsQueries.insert(
            id = matchReportId,
            home = 0,
            away = 0,
            mvp = 0,
            best_player = null,
            tour_id = TourId(1),
            updated_at = LocalDateTime.now(clock),
            phase = Phase.PlayOff,
        )
        setQueries.insert(
            number = 1,
            home_score = 2,
            away_score = 3,
            start_time = OffsetDateTime.now(clock),
            end_time = OffsetDateTime.now(clock),
            duration = Duration.ZERO,
            match_statistics_id = matchReportId,
        )
        matchQueries.updateMatchReport(
            id = matchId,
            match_statistics_id = matchReportId,
        )
        matchStorage.insertOrUpdate(listOf(match), tour.id)

        // THEN
        val newValue = matchQueries.selectAllMatchesByTour(tour.id).executeAsOne()
        val newExpectedValue = selectAllMatchesByTourOf(
            id = matchId,
            state = MatchState.PotentiallyFinished,
            match_statistics_id = matchReportId,
            date = null,
            end_time = OffsetDateTime.now(clock),
            winner_team_id = null,
        )
        assert(newValue == newExpectedValue)
    }

    @Test
    fun `insert more matches works properly`() = runBlocking {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = configure(season, league)
        val size = 10
        val matches = (1..size).map { index -> potentiallyFinishedOf(matchIdOf(index.toLong())) }

        // WHEN
        matchStorage.insertOrUpdate(matches, tour.id)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tour.id).executeAsList()
        assert(value.size == size)
    }

    @Test
    fun `insert multiple finished matches works properly`() = runBlocking {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = configure(season, league)
        val size = 2
        val teams = (1..size*size).map { index ->
            matchTeamOf(
                teamId = teamIdOf(index.toLong()),
                players = listOf(matchPlayerOf()),
            )
        }.toMutableList()
        teams.map { matchTeam ->
            InsertTeam(
                team = teamOf(id = matchTeam.teamId),
                tour = tour,
            )
        }.forEach { insertTeam ->
            insert(insertTeam)
            insert(
                InsertPlayer(
                    player = playerWithDetailsOf(teamPlayer = teamPlayerOf(team = insertTeam.team.id)),
                    tour = tour,
                )
            )
        }
        val now = OffsetDateTime.now(clock)
        val range = (1..size)
        val matches = (1..size + 1).map { index -> potentiallyFinishedOf(matchIdOf(index.toLong())) }
        val matchStats = range.map { index ->
            matchStatisticsOf(
                away = teams.removeFirst(),
                home = teams.removeFirst(),
                matchReportId = matchReportIdOf(index.toLong()),
                sets = (1..index + 3).map { setIndex ->
                    matchSetOf(
                        number = setIndex,
                        endTime = now.plusHours(setIndex.toLong()),
                        score = Score(home = 0, away = 25)
                    )
                }
            )
        }

        // WHEN
        matchStorage.insertOrUpdate(matches, tour.id)
        matchStats.forEachIndexed { index, matchStatistics ->
            storage.insert(matchStatistics, tour.id, matches[index].id)
        }

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tour.id).executeAsList()
        assert(value.size == matches.size)
        value.forEachIndexed { index, selectAllMatchesByTour ->
            if (index == 0 || index == 1) {
                assert(selectAllMatchesByTour.match_statistics_id == matchStats[index].matchReportId)
                assert(selectAllMatchesByTour.end_time == matchStats[index].sets.last().endTime)
                assert(selectAllMatchesByTour.winner_team_id == matchStats[index].away.teamId)
            } else {
                assert(
                    selectAllMatchesByTour == selectAllMatchesByTourOf(
                        id = matches[index].id,
                        state = MatchState.PotentiallyFinished,
                    )
                )
            }
        }
    }
}

private fun selectAllMatchesByTourOf(
    id: MatchId = matchIdOf(),
    state: MatchState = MatchState.Scheduled,
    date: OffsetDateTime? = null,
    match_statistics_id: MatchReportId? = null,
    end_time: OffsetDateTime? = null,
    winner_team_id: TeamId? = null,
    home_id: TeamId = teamIdOf(),
    away_id: TeamId = teamIdOf(),
): SelectAllMatchesByTour = SelectAllMatchesByTour(
    id = id,
    date = date,
    match_statistics_id = match_statistics_id,
    end_time = end_time,
    state = state,
    winner_team_id = winner_team_id,
    home_id = home_id,
    away_id = away_id,
)

fun matchStorageOf(
    insertOrUpdate: InsertMatchesResult = InsertMatchesResult.success(Unit),
    getAllMatches: Flow<List<AllMatchesItem>> = flowOf(emptyList()),
): MatchStorage = object : MatchStorage {
    override suspend fun insertOrUpdate(matches: List<AllMatchesItem>, tourId: TourId): InsertMatchesResult = insertOrUpdate
    override suspend fun getAllMatches(tourId: TourId): Flow<List<AllMatchesItem>> = getAllMatches
}