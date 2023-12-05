package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.datetime.ZonedDateTime
import com.kamilh.volleyballstats.domain.assertFailure
import com.kamilh.volleyballstats.domain.assertSuccess
import com.kamilh.volleyballstats.domain.leagueOf
import com.kamilh.volleyballstats.domain.matchIdOf
import com.kamilh.volleyballstats.domain.matchOf
import com.kamilh.volleyballstats.domain.matchReportOf
import com.kamilh.volleyballstats.domain.matchSetOf
import com.kamilh.volleyballstats.domain.matchTeamOf
import com.kamilh.volleyballstats.domain.models.League
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.Phase
import com.kamilh.volleyballstats.domain.models.Score
import com.kamilh.volleyballstats.domain.models.Season
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.domain.models.Tour
import com.kamilh.volleyballstats.domain.models.TourId
import com.kamilh.volleyballstats.domain.player.playerOf
import com.kamilh.volleyballstats.domain.playerIdOf
import com.kamilh.volleyballstats.domain.seasonOf
import com.kamilh.volleyballstats.domain.teamIdOf
import com.kamilh.volleyballstats.domain.teamOf
import com.kamilh.volleyballstats.domain.tourIdOf
import com.kamilh.volleyballstats.domain.tourOf
import com.kamilh.volleyballstats.domain.utils.CurrentDate
import com.kamilh.volleyballstats.storage.databse.SelectAllMatchesByTour
import com.kamilh.volleyballstats.utils.localDateTime
import com.kamilh.volleyballstats.utils.zonedDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class SqlMatchStorageTest : ReportStorageTest() {

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
    fun `insert returns TourNotFound when there is no tour in the database`() = runTest {
        // GIVEN
        val tourId = tourIdOf()

        // WHEN
        val result = matchStorage.insertOrUpdate(emptyList(), tourId)

        // THEN
        result.assertFailure {
            assertEquals(expected = InsertMatchesError.TourNotFound, this)
        }
    }

    @Test
    fun `insert returns Success when there is a tour in the database`() = runTest {
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
    fun `hasReport is true after match has report in the database`() = runTest {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = configure(season, league)
        val matchId = matchIdOf(1)
        val match = matchOf(id = matchId)
        matchStorage.insertOrUpdate(listOf(match), tour.id)
        val matchStatistics = load(league = league, season = season, matchId = matchId)

        // WHEN
        val result = matchStorage.getAllMatches(tour.id).first().first()

        // THEN
        assertEquals(expected = matchStatistics.matchId, result.id)
        assertTrue(result.hasReport)
    }

    @Test
    fun `insert updates a value properly`() = runTest {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = configure(season, league)
        val matchId = matchIdOf(2)
        val date = zonedDateTime()
        val match = matchOf(id = matchId, date = date)

        // WHEN
        matchStorage.insertOrUpdate(listOf(match), tour.id)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tour.id).executeAsOne()
        val expectedValue = selectAllMatchesByTourOf(id = matchId, date = date)
        assertEquals(expected = expectedValue, value)

        // WHEN
        val newDate = zonedDateTime().plus(1.days)
        val newMatch = matchOf(id = matchId, date = newDate)
        matchStorage.insertOrUpdate(listOf(newMatch), tour.id)

        // THEN
        val newValue = matchQueries.selectAllMatchesByTour(tour.id).executeAsOne()
        val newExpectedValue = selectAllMatchesByTourOf(id = matchId, date = newDate)
        assertEquals(expected = newExpectedValue, newValue)
    }

    @Test
    fun `insert updates a match that already has associated MatchReport value properly`() = runTest {
        // GIVEN
        val tour = configure()
        val matchId = matchIdOf(2)
        val match = matchOf(id = matchId)

        // WHEN
        matchStorage.insertOrUpdate(listOf(match), tour.id)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tour.id).executeAsOne()
        val expectedValue = selectAllMatchesByTourOf(id = matchId)
        assertEquals(expected = expectedValue, value)

        // WHEN
        matchReportQueries.insert(
            id = matchId,
            home = 0,
            away = 0,
            mvp = 0,
            best_player = null,
            tour_id = TourId(1),
            updated_at = localDateTime(),
            phase = Phase.PlayOff,
        )
        setQueries.insert(
            number = 1,
            home_score = 2,
            away_score = 3,
            start_time = zonedDateTime(),
            end_time = zonedDateTime(),
            duration = Duration.ZERO,
            match_id = matchId,
        )
        matchStorage.insertOrUpdate(listOf(match), tour.id)

        // THEN
        val newValue = matchQueries.selectAllMatchesByTour(tour.id).executeAsOne()
        val newExpectedValue = selectAllMatchesByTourOf(id = matchId, date = zonedDateTime(), match_statistics_id = matchId)
        assertEquals(expected = newExpectedValue, newValue)
    }

    @Test
    fun `insert more matches works properly`() = runTest {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = configure(season, league)
        val size = 10
        val matches = (1..size).map { index -> matchOf(matchIdOf(index.toLong())) }

        // WHEN
        matchStorage.insertOrUpdate(matches, tour.id)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tour.id).executeAsList()
        assertEquals(expected = size, value.size)
    }

    @Test
    fun `insert multiple finished matches works properly`() = runTest {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = configure(season, league)
        val size = 2
        val teams = (1..size*size).map { index ->
            matchTeamOf(
                teamId = teamIdOf(index.toLong()),
                players = listOf(playerIdOf()),
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
                    player = playerOf(team = insertTeam.team.id),
                    tour = tour,
                )
            )
        }
        val now = zonedDateTime()
        val range = (1..size)
        val matches = (1..size + 1).map { index -> matchOf(matchIdOf(index.toLong())) }
        val matchStats = range.map { index ->
            matchReportOf(
                away = teams.removeFirst(),
                home = teams.removeFirst(),
                matchId = matchIdOf(index.toLong()),
                sets = (1..index + 3).map { setIndex ->
                    matchSetOf(
                        number = setIndex,
                        endTime = now.plus(setIndex.hours),
                        score = Score(home = 0, away = 25)
                    )
                }
            )
        }

        // WHEN
        matchStorage.insertOrUpdate(matches, tour.id)
        matchStats.forEach { matchStatistics ->
            storage.insert(matchStatistics, tour.id)
        }

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tour.id).executeAsList()
        assertEquals(expected = matches.size, value.size)
        value.forEachIndexed { index, selectAllMatchesByTour ->
            if (index == 0 || index == 1) {
                assertEquals(expected = matchStats[index].matchId, selectAllMatchesByTour.id)
                assertTrue(selectAllMatchesByTour.match_statistics_id != null)
            } else {
                assertEquals(expected = selectAllMatchesByTourOf(id = matches[index].id), selectAllMatchesByTour)
            }
        }
    }

    @Test
    fun `deleteInvalidMatches deletes week old matches whose time is set to midnight`() = runTest {
        // GIVEN
        val season = seasonOf()
        val league = leagueOf()
        val tour = configure(season, league)
        val now = CurrentDate.zonedDateTime
        val matches = listOf(
            matchOf(id = MatchId(0), date = now.withMidnightTime()),
            matchOf(id = MatchId(1), date = now.minus(7.days).withMidnightTime()), // should be removed
            matchOf(id = MatchId(2), date = now.minus(7.days)),
            matchOf(id = MatchId(3), date = now.minus(4.days).withMidnightTime()),
            matchOf(id = MatchId(4), date = now.minus(4.days)),
            matchOf(id = MatchId(5), date = now.minus(14.days).withMidnightTime()), // should be removed
            matchOf(id = MatchId(6), date = now.minus(14.days)),
        )
        matchStorage.insertOrUpdate(matches, tour.id)

        // WHEN
        matchStorage.deleteInvalidMatches(tour.id)

        // THEN
        val value = matchQueries.selectAllMatchesByTour(tour.id).executeAsList()
        val expectedIds = listOf(MatchId(0), MatchId(2), MatchId(3), MatchId(4), MatchId(6))
        assertEquals(expected = expectedIds, value.map { it.id })
    }
}

private fun selectAllMatchesByTourOf(
    id: MatchId = matchIdOf(),
    date: ZonedDateTime? = zonedDateTime(),
    home_id: TeamId = teamIdOf(),
    away_id: TeamId = teamIdOf(),
    match_statistics_id: MatchId? = null
): SelectAllMatchesByTour = SelectAllMatchesByTour(
    id = id,
    date = date,
    home_id = home_id,
    away_id = away_id,
    match_statistics_id = match_statistics_id,
)