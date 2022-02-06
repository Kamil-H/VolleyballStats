package com.kamilh.storage

import com.kamilh.databse.SelectAllMatchesByTour
import com.kamilh.models.*
import com.kamilh.repository.polishleague.tourYearOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.time.Duration

class SqlMatchStorageTest : StatisticsStorageTest() {

    private val matchStorage by lazy {
        SqlMatchStorage(
            queryRunner = testQueryRunner,
            tourQueries = tourQueries,
            matchQueries = matchQueries,
        )
    }

    private fun configure(
        tourYear: TourYear = tourYearOf(),
        league: League = leagueOf(),
    ) {
        val tour = tourOf(year = tourYear, league = league)
        insert(league)
        insert(tour)
        insert(
            InsertTeam(
                team = teamOf(),
                league = league,
                tourYear = tourYear,
            )
        )
    }

    @Test
    fun `insert returns TourNotFound when there is no tour in the database`() = runBlocking {
        // GIVEN
        val tourYear = tourYearOf()
        val league = leagueOf()

        // WHEN
        val result = matchStorage.insertOrUpdate(emptyList(), league, tourYear)

        // THEN
        result.assertFailure {
            assert(this == InsertMatchesError.TourNotFound)
        }
    }

    @Test
    fun `insert returns Success when there is a tour in the database`() = runBlocking {
        // GIVEN
        val tourYear = tourYearOf()
        val league = leagueOf()
        val tour = tourOf(year = tourYear, league = league)
        insert(league)
        insert(tour)

        // WHEN
        val result = matchStorage.insertOrUpdate(emptyList(), league, tourYear)

        // THEN
        result.assertSuccess()
    }

    @Test
    fun `insert select returns Saved when there is MatchReport associated with the Match`() = runBlocking {
        // GIVEN
        val tourYear = tourYearOf()
        val league = leagueOf()
        configure(tourYear, league)
        val matchId = matchIdOf(1)
        val match = potentiallyFinishedOf(id = matchId)
        matchStorage.insertOrUpdate(listOf(match), league, tourYear)
        val matchStatistics = load(league = league, tourYear = tourYear, matchId = matchId)

        // WHEN
        val result = matchStorage.getAllMatches(league, tourYear).first().first()

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
        val tourYear = tourYearOf()
        val league = leagueOf()
        configure(tourYear, league)
        val matchId = matchIdOf(2)
        val match = notScheduledOf(id = matchId)

        // WHEN
        matchStorage.insertOrUpdate(listOf(match), league, tourYear)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tourYear, league.country, league.division).executeAsOne()
        val expectedValue = selectAllMatchesByTourOf(id = matchId, state = MatchState.NotScheduled)
        assert(value == expectedValue)
    }

    @Test
    fun `insert PotentiallyFinished works properly`() = runBlocking {
        // GIVEN
        val tourYear = tourYearOf()
        val league = leagueOf()
        configure(tourYear, league)
        val matchId = matchIdOf(2)
        val match = potentiallyFinishedOf(id = matchId)

        // WHEN
        matchStorage.insertOrUpdate(listOf(match), league, tourYear)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tourYear, league.country, league.division).executeAsOne()
        val expectedValue = selectAllMatchesByTourOf(id = matchId, state = MatchState.PotentiallyFinished)
        assert(value == expectedValue)
    }

    @Test
    fun `insert Scheduled works properly`() = runBlocking {
        // GIVEN
        val tourYear = tourYearOf()
        val league = leagueOf()
        configure(tourYear, league)
        val matchId = matchIdOf(2)
        val date = OffsetDateTime.now(clock)
        val match = scheduledOf(id = matchId, date = date)

        // WHEN
        matchStorage.insertOrUpdate(listOf(match), league, tourYear)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tourYear, league.country, league.division).executeAsOne()
        val expectedValue = selectAllMatchesByTourOf(id = matchId, date = date, state = MatchState.Scheduled)
        assert(value == expectedValue)
    }

    @Test
    fun `insert Saved returns error`() = runBlocking {
        // GIVEN
        val tourYear = tourYearOf()
        val league = leagueOf()
        val tour = tourOf(year = tourYear, league = league)
        insert(league)
        insert(tour)
        val matchId = matchIdOf(2)
        val match = savedOf(id = matchId)

        // WHEN
        val result = matchStorage.insertOrUpdate(listOf(match), league, tourYear)

        // THEN
        result.assertFailure {
            require(this is InsertMatchesError.TryingToInsertSavedItems)
            assert(this.saved == listOf(match))
        }
    }

    @Test
    fun `insert updates a value properly`() = runBlocking {
        // GIVEN
        val tourYear = tourYearOf()
        val league = leagueOf()
        configure(tourYear, league)
        val matchId = matchIdOf(2)
        val date = OffsetDateTime.now(clock)
        val match = scheduledOf(id = matchId, date = date)

        // WHEN
        matchStorage.insertOrUpdate(listOf(match), league, tourYear)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tourYear, league.country, league.division).executeAsOne()
        val expectedValue = selectAllMatchesByTourOf(id = matchId, date = date, state = MatchState.Scheduled)
        assert(value == expectedValue)

        // WHEN
        val newDate = null
        val newMatch = potentiallyFinishedOf(id = matchId, date = newDate)
        matchStorage.insertOrUpdate(listOf(newMatch), league, tourYear)

        // THEN
        val newValue = matchQueries.selectAllMatchesByTour(tourYear, league.country, league.division).executeAsOne()
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
        val tourYear = tourYearOf()
        val league = leagueOf()
        configure()
        val matchId = matchIdOf(2)
        val match = potentiallyFinishedOf(id = matchId)
        val matchReportId = matchReportIdOf(1)

        // WHEN
        matchStorage.insertOrUpdate(listOf(match), league, tourYear)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tourYear, league.country, league.division).executeAsOne()
        val expectedValue = selectAllMatchesByTourOf(id = matchId, state = MatchState.PotentiallyFinished)
        assert(value == expectedValue)

        // WHEN
        matchStatisticsQueries.insert(
            id = matchReportId,
            home = 0,
            away = 0,
            mvp = 0,
            best_player = null,
            tour_id = 1,
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
        matchStorage.insertOrUpdate(listOf(match), league, tourYear)

        // THEN
        val newValue = matchQueries.selectAllMatchesByTour(tourYear, league.country, league.division).executeAsOne()
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
        val tourYear = tourYearOf()
        val league = leagueOf()
        configure(tourYear, league)
        val size = 10
        val matches = (1..size).map { index -> potentiallyFinishedOf(matchIdOf(index.toLong())) }

        // WHEN
        matchStorage.insertOrUpdate(matches, league, tourYear)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tourYear, league.country, league.division).executeAsList()
        assert(value.size == size)
    }

    @Test
    fun `insert multiple finished matches works properly`() = runBlocking {
        // GIVEN
        val tourYear = tourYearOf()
        val league = leagueOf()
        configure(tourYear, league)
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
                league = league,
                tourYear = tourYear,
            )
        }.forEach { insertTeam ->
            insert(insertTeam)
            insert(
                InsertPlayer(
                    player = playerWithDetailsOf(teamPlayer = playerOf(team = insertTeam.team.id)),
                    league = league,
                    tourYear = tourYear,
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
        matchStorage.insertOrUpdate(matches, league, tourYear)
        matchStats.forEachIndexed { index, matchStatistics ->
            storage.insert(matchStatistics, league, tourYear, matches[index].id)
        }

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tourYear, league.country, league.division).executeAsList()
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