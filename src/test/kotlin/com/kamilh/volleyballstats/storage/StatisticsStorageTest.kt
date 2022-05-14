package com.kamilh.volleyballstats.storage

import com.kamilh.volleyballstats.match_analyzer.*
import com.kamilh.volleyballstats.match_analyzer.strategies.*
import com.kamilh.volleyballstats.models.*
import com.kamilh.volleyballstats.repository.polishleague.seasonOf
import com.kamilh.volleyballstats.utils.testAppDispatchers
import kotlinx.coroutines.flow.first
import com.kamilh.volleyballstats.storage.TestQueryRunner
import com.kamilh.volleyballstats.storage.testQueryRunner

abstract class StatisticsStorageTest : DatabaseTest() {

    protected val storage: SqlMatchStatisticsStorage by lazy {
        SqlMatchStatisticsStorage(
            queryRunner = testQueryRunner,
            teamQueries = teamQueries,
            teamPlayerQueries = teamPlayerQueries,
            tourTeamQueries = tourTeamQueries,
            matchStatisticsQueries = matchStatisticsQueries,
            playQueries = playQueries,
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
            tourQueries = tourQueries,
        )
    }

    private val analyzer: MatchReportAnalyzerInteractor by lazy {
        MatchReportAnalyzerInteractor(
            appDispatchers = testAppDispatchers,
            teamStorage = SqlTeamStorage(
                queryRunner = TestQueryRunner(),
                teamQueries = teamQueries,
                tourTeamQueries = tourTeamQueries,
                tourQueries = tourQueries,
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
        season: Season = seasonOf(2020),
        matchId: MatchId = matchIdOf(),
    ): MatchStatistics {
        // GIVEN
        val fileName = "${matchReportId.value}.json"
        val matchReport = loadMatchReportFile(fileName)
        val tour = tourOf(league = league, season = season)

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
                tour = tour,
            )
        )
        insert(
            InsertTeam(
                team = teamOf(
                    id = awayId,
                    name = matchReport.matchTeams.away.name
                ),
                tour = tour,
            )
        )
        matchReport.matchTeams.home.players.forEach { teamPlayer ->
            insert(
                InsertPlayer(
                    tour = tour,
                    player = playerWithDetailsOf(
                        teamPlayer = teamPlayerOf(
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
                    tour = tour,
                    player = playerWithDetailsOf(
                        teamPlayer = teamPlayerOf(
                            id = teamPlayer.id,
                            team = awayId,
                        ),
                    )
                )
            )
        }
        val matchStatistics = analyzer(MatchReportAnalyzerParams(matchId, matchReport, tour)).value!!

        // WHEN
        val insertResult = storage.insert(matchStatistics, tour.id)

        // THEN
        val result = matchStatisticsQueries.selectAll().executeAsList()
        assert(storage.getAllMatchStatistics().first().first() == matchStatistics)
        assert(result.isNotEmpty())
        insertResult.assertSuccess()
        return matchStatistics
    }
}