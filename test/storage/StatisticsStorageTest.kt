package com.kamilh.storage

import app.cash.turbine.test
import com.kamilh.match_analyzer.MatchReportAnalyzer
import com.kamilh.match_analyzer.analyzeErrorReporterOf
import com.kamilh.match_analyzer.eventsPreparerOf
import com.kamilh.match_analyzer.loadMatchReportFile
import com.kamilh.match_analyzer.strategies.*
import com.kamilh.models.*
import com.kamilh.repository.polishleague.tourYearOf

abstract class StatisticsStorageTest : DatabaseTest() {

    protected val storage: SqlMatchStatisticsStorage by lazy {
        SqlMatchStatisticsStorage(
            queryRunner = testQueryRunner,
            teamQueries = teamQueries,
            tourQueries = tourQueries,
            teamPlayerQueries = teamPlayerQueries,
            tourTeamQueries = tourTeamQueries,
            matchStatisticsQueries = matchStatisticsQueries,
            playQueries = playQueries,
            playerQueries = playerQueries,
            playAttackQueries = playAttackQueries,
            playBlockQueries = playBlockQueries,
            playDigQueries = playDigQueries,
            playFreeballQueries = playFreeballQueries,
            playReceiveQueries = playReceiveQueries,
            playServeQueries = playServeQueries,
            playSetQueries = playSetQueries,
            pointQueries = pointQueries,
            pointLineupQueries = pointLineupQueries,
            setQueries = setQueries,
            matchAppearanceQueries = matchAppearanceQueries,
            matchQueries = matchQueries,
        )
    }

    private val analyzer: MatchReportAnalyzer by lazy {
        MatchReportAnalyzer(
            teamStorage = SqlTeamStorage(
                queryRunner = TestQueryRunner(),
                teamQueries = teamQueries,
                tourTeamQueries = tourTeamQueries,
            ),
            strategies = listOf(AttackStrategy(), BlockStrategy(), DigStrategy(), FreeballStrategy(), ReceiveStrategy(),
                ServeStrategy(), SetStrategy()
            ),
            preparer = eventsPreparerOf(),
            analyzeErrorReporter = analyzeErrorReporterOf(),
        )
    }

    /**
     * Loads a requested [MatchReport] from json file and inserts it into database. At the same time it tests whether
     * insert and select works properly.
     */
    protected suspend fun load(
        matchReportId: MatchReportId = matchReportIdOf(2101911L),
        homeId: TeamId = teamIdOf(2101911),
        awayId: TeamId = teamIdOf(1),
        league: League = leagueOf(),
        tourYear: TourYear = tourYearOf(2020),
        matchId: MatchId = matchIdOf(),
    ): MatchStatistics {
        // GIVEN
        val fileName = "${matchReportId.value}.json"
        val matchReport = loadMatchReportFile(fileName)
        val tour = tourOf(league = league, year = tourYear)

        teamQueries.insert(homeId)
        teamQueries.insert(awayId)

        insert(league)
        insert(tour)
        insert(
            InsertTeam(
                team = teamOf(
                    id = homeId,
                    name = matchReport.matchTeams.home.name
                ),
                tourYear = tourYear,
                league = league,
            )
        )
        insert(
            InsertTeam(
                team = teamOf(
                    id = awayId,
                    name = matchReport.matchTeams.away.name
                ),
                tourYear = tourYear,
                league = league,
            )
        )
        matchReport.matchTeams.home.players.forEach { teamPlayer ->
            insert(
                InsertPlayer(
                    league = league,
                    tourYear = tourYear,
                    player = playerWithDetailsOf(
                        player = playerOf(
                            id = teamPlayer.id,
                            team = homeId,
                        ),
                    )
                )
            )
        }
        matchReport.matchTeams.away.players.forEach { teamPlayer ->
            insert(
                InsertPlayer(
                    league = league,
                    tourYear = tourYear,
                    player = playerWithDetailsOf(
                        player = playerOf(
                            id = teamPlayer.id,
                            team = awayId,
                        ),
                    )
                )
            )
        }
        val matchStatistics = analyzer.analyze(matchReport, tourYear, league)

        // WHEN
        val insertResult = storage.insert(matchStatistics, league, tourYear, matchId)

        // THEN
        val result = matchStatisticsQueries.selectAll().executeAsList()
        storage.getAllMatchStatistics(league, tourYear).test {
            assert(awaitItem().first() == matchStatistics)
        }
        assert(result.isNotEmpty())
        insertResult.assertSuccess()
        return matchStatistics
    }
}