package com.kamilh.storage

import com.kamilh.databse.SelectAllMatchesByTour
import com.kamilh.models.*
import com.kamilh.repository.polishleague.tourYearOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDateTime
import java.time.OffsetDateTime

class SqlMatchStorageTest : StatisticsStorageTest() {

    private val matchStorage by lazy {
        SqlMatchStorage(
            queryRunner = testQueryRunner,
            tourQueries = tourQueries,
            matchQueries = matchQueries,
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
        val tour = tourOf(league = league, year = tourYear)
        val matchId = matchIdOf(1)
        val match = AllMatchesItem.PotentiallyFinished(matchId)
        insert(league)
        insert(tour)
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
        val tour = tourOf(year = tourYear, league = league)
        insert(league)
        insert(tour)
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
        val tour = tourOf(year = tourYear, league = league)
        insert(league)
        insert(tour)
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
        val tour = tourOf(year = tourYear, league = league)
        insert(league)
        insert(tour)
        val matchId = matchIdOf(2)
        val date = LocalDateTime.now()
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
            require(this is InsertMatchesError.TryingToSaveSavedItems)
            assert(this.saved == listOf(match))
        }
    }

    @Test
    fun `insert updates a value properly`() = runBlocking {
        // GIVEN
        val tourYear = tourYearOf()
        val league = leagueOf()
        val tour = tourOf(year = tourYear, league = league)
        insert(league)
        insert(tour)
        val matchId = matchIdOf(2)
        val date = LocalDateTime.now()
        val match = scheduledOf(id = matchId, date = date)

        // WHEN
        matchStorage.insertOrUpdate(listOf(match), league, tourYear)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tourYear, league.country, league.division).executeAsOne()
        val expectedValue = selectAllMatchesByTourOf(id = matchId, date = date, state = MatchState.Scheduled)
        assert(value == expectedValue)

        // WHEN
        val newMatch = potentiallyFinishedOf(id = matchId)
        matchStorage.insertOrUpdate(listOf(newMatch), league, tourYear)

        // THEN
        val newValue = matchQueries.selectAllMatchesByTour(tourYear, league.country, league.division).executeAsOne()
        val newExpectedValue = selectAllMatchesByTourOf(id = matchId, date = null, state = MatchState.PotentiallyFinished)
        assert(newValue == newExpectedValue)
    }
}

private fun selectAllMatchesByTourOf(
    id: MatchId = matchIdOf(),
    state: MatchState = MatchState.Scheduled,
    date: LocalDateTime? = null,
    match_statistics_id: MatchReportId? = null,
    tour_id: Long = 1,
    end_time: OffsetDateTime? = null,
    MAX: Long? = null,
    winner_team_id: TeamId? = null,
): SelectAllMatchesByTour = SelectAllMatchesByTour(
    id = id,
    MAX = MAX,
    date = date,
    match_statistics_id = match_statistics_id,
    end_time = end_time,
    state = state,
    tour_id = tour_id,
    winner_team_id = winner_team_id,
)