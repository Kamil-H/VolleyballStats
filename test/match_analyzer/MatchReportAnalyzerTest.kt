package com.kamilh.match_analyzer

import com.kamilh.extensions.atPolandOffset
import com.kamilh.match_analyzer.strategies.PlayActionStrategy
import com.kamilh.models.*
import com.kamilh.repository.models.MatchResponse
import com.kamilh.repository.models.mappers.MatchResponseToMatchReportMapper
import com.kamilh.repository.polishleague.tourYearOf
import com.kamilh.storage.TeamStorage
import com.kamilh.storage.teamStorageOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import match_analyzer.lineupOf
import org.junit.Test
import java.io.File
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.time.Duration

class MatchReportAnalyzerTest {

    private val tour = tourYearOf()
    private fun analyzer(
        teamStorage: TeamStorage = teamStorageOf(),
        strategies: List<PlayActionStrategy<*>> = emptyList(),
        preparer: EventsPreparer = eventsPreparerOf(),
        analyzeErrorReporter: AnalyzeErrorReporter = analyzeErrorReporterOf(),
    ): MatchReportAnalyzer = MatchReportAnalyzer(
        teamStorage = teamStorage,
        strategies = strategies,
        preparer = preparer,
        analyzeErrorReporter = analyzeErrorReporter,
    )

    @Test(expected = IllegalStateException::class)
    fun `test that Exception is thrown when there are more Sets in Scout than in ScoutData`() = runBlockingTest {
        // GIVEN
        val matchReport = matchReportOf(
            scout = scoutOf(
                sets = listOf(setOf())
            ),
            scoutData = listOf()
        )
        val errors: MutableList<AnalyzeError> = mutableListOf()

        // WHEN
        analyzer(analyzeErrorReporter = analyzeErrorReporterOf(errors)).analyze(matchReport, tour)

        // THEN
        assert(errors.filterIsInstance<AnalyzeError.WrongSetsCount>().isNotEmpty())
    }

    @Test(expected = IllegalStateException::class)
    fun `test that Exception is thrown when there are no such Home team in the Storage`() = runBlockingTest {
        // GIVEN
        val home = "home"
        val matchReport = matchReportOf(
            matchTeams = matchTeamsOf(
                home = matchReportTeamOf(
                    name = home
                )
            )
        )
        val errors: MutableList<AnalyzeError> = mutableListOf()

        // WHEN
        analyzer(analyzeErrorReporter = analyzeErrorReporterOf(errors)).analyze(matchReport, tour)

        // THEN
        val teamNotFound = errors.filterIsInstance<AnalyzeError.TeamNotFound>().firstOrNull()
        require(teamNotFound != null)
        assert(teamNotFound.teamName == home)
    }

    @Test(expected = IllegalStateException::class)
    fun `test that Exception is thrown when there are no such Away team in the Storage`() = runBlockingTest {
        // GIVEN
        val home = "home"
        val homeTeam = teamOf(name = home)
        val away = "away"
        val matchReport = matchReportOf(
            matchTeams = matchTeamsOf(
                away = matchReportTeamOf(name = away),
                home = matchReportTeamOf(name = home),
            )
        )
        val errors: MutableList<AnalyzeError> = mutableListOf()

        // WHEN
        analyzer(
            analyzeErrorReporter = analyzeErrorReporterOf(errors),
            teamStorage = teamStorageOf(getTeam = listOf(homeTeam)),
        ).analyze(matchReport, tour)

        // THEN
        val teamNotFound = errors.filterIsInstance<AnalyzeError.TeamNotFound>().firstOrNull()
        require(teamNotFound != null)
        assert(teamNotFound.teamName == away)
    }

    @Test
    fun `test values are calculated properly`() = runBlocking {
        // GIVEN
        val matchReportId = 2101911L
        val fileName = "$matchReportId.json"
        val matchReport = loadMatchReportFile(fileName)
        val teamStorage = teamStorageOf(
            getTeam = listOf(
                teamOf(
                    name = "VERVA Warszawa ORLEN Paliwa",
                    id = teamIdOf(0),
                ),
                teamOf(
                    name = "Stal Nysa",
                    id = teamIdOf(1),
                )
            )
        )

        // WHEN
        val matchStatistics = analyzer(teamStorage = teamStorage).analyze(matchReport, tourYearOf())

        // THEN
        assert(matchStatistics.matchReportId.value == matchReportId)
        assert(matchStatistics.home.teamId.value == 0L)
        assert(matchStatistics.away.teamId.value == 1L)
        assert(matchStatistics.mvp.value == 22573L)
        assert(matchStatistics.bestPlayer?.value == 2100768L)

        val setTimes = listOf(
            matchTimeOf(startTime="2021-01-05T16:36", endTime="2021-01-05T16:55", duration=19),
            matchTimeOf(startTime="2021-01-05T17:00", endTime="2021-01-05T17:29", duration=29),
            matchTimeOf(startTime="2021-01-05T17:34", endTime="2021-01-05T18:06", duration=32),
            matchTimeOf(startTime="2021-01-05T18:11", endTime="2021-01-05T18:40", duration=29),
            matchTimeOf(startTime="2021-01-05T18:45", endTime="2021-01-05T19:12", duration=27),
        )
        val firstSetScores = listOf(
            scoreOf(home=0, away=1), scoreOf(home=0, away=2), scoreOf(home=1, away=2), scoreOf(home=2, away=2), scoreOf(home=3, away=2),
            scoreOf(home=3, away=3), scoreOf(home=4, away=3), scoreOf(home=5, away=3), scoreOf(home=5, away=4), scoreOf(home=6, away=4),
            scoreOf(home=6, away=5), scoreOf(home=7, away=5), scoreOf(home=8, away=5), scoreOf(home=8, away=6), scoreOf(home=8, away=7),
            scoreOf(home=9, away=7), scoreOf(home=9, away=8), scoreOf(home=10, away=8), scoreOf(home=11, away=8), scoreOf(home=12, away=8),
            scoreOf(home=12, away=9), scoreOf(home=13, away=9), scoreOf(home=14, away=9), scoreOf(home=15, away=9), scoreOf(home=16, away=9),
            scoreOf(home=16, away=10), scoreOf(home=17, away=10), scoreOf(home=18, away=10), scoreOf(home=19, away=10), scoreOf(home=20, away=10),
            scoreOf(home=21, away=10), scoreOf(home=22, away=10), scoreOf(home=23, away=10), scoreOf(home=24, away=10), scoreOf(home=25, away=10),
        )
        val firstSetAwayLineups = listOf(
            listOf(51, 561, 634, 188, 2100768, 27988),
            listOf(561, 634, 188, 2100768, 27988, 274),
            listOf(561, 634, 188, 2100768, 27988, 274),
            listOf(561, 634, 188, 2100768, 27988, 51),
            listOf(561, 634, 188, 2100768, 27988, 51),
            listOf(561, 634, 188, 2100768, 27988, 51),
            listOf(634, 188, 2100768, 27988, 274, 561),
            listOf(634, 188, 2100768, 27988, 51, 561),
            listOf(634, 188, 2100768, 27988, 51, 561),
            listOf(188, 2100768, 27988, 648, 561, 634),
            listOf(51, 2100768, 27988, 648, 561, 634),
            listOf(2100768, 27988, 648, 561, 634, 274),
            listOf(2100768, 27988, 648, 561, 634, 51),
            listOf(2100768, 27988, 648, 561, 634, 51),
            listOf(27988, 648, 561, 634, 274, 2100768),
            listOf(27988, 648, 561, 634, 274, 2100768),
            listOf(27988, 648, 561, 634, 51, 2100768),
            listOf(648, 561, 634, 188, 2100768, 27988),
            listOf(51, 561, 634, 188, 2100768, 27988),
            listOf(51, 561, 634, 188, 2100768, 27988),
            listOf(51, 561, 634, 188, 2100768, 27988),
            listOf(561, 634, 188, 2100768, 27988, 51),
            listOf(561, 634, 188, 2100768, 27988, 51),
            listOf(561, 634, 188, 2100768, 27988, 51),
            listOf(561, 634, 188, 2100768, 27988, 51),
            listOf(561, 634, 188, 2100768, 27988, 51),
            listOf(634, 188, 2100768, 27988, 274, 561),
            listOf(634, 188, 2100768, 27988, 51, 561),
            listOf(634, 188, 2100768, 27988, 51, 561),
            listOf(634, 188, 474, 27988, 51, 452),
            listOf(634, 188, 474, 27988, 51, 452),
            listOf(634, 188, 474, 27988, 51, 452),
            listOf(634, 188, 474, 27988, 51, 452),
            listOf(634, 328, 474, 27988, 51, 452),
            listOf(634, 328, 474, 27988, 51, 452),
        )
        matchStatistics.sets.forEachIndexed { index, matchSet ->
            val matchTime = setTimes[index]
            assert(matchTime.startTime == matchSet.startTime)
            assert(matchTime.endTime == matchSet.endTime)
            assert(matchTime.duration == matchSet.duration)

            assert(matchSet.score == matchStatistics.sets[index].score)
            if (index == 0) {
                matchSet.points.forEachIndexed { pointIndex, matchPoint ->
                    assert(matchPoint.score == firstSetScores[pointIndex])
                    assert(matchPoint.awayLineup == lineupOf(firstSetAwayLineups[pointIndex]))
                }
            }
        }
    }

    private fun matchTimeOf(
        startTime: String,
        endTime: String,
        duration: Int,
    ): MatchTime = MatchTime(
        startTime = LocalDateTime.parse(startTime).atPolandOffset(),
        endTime = LocalDateTime.parse(endTime).atPolandOffset(),
        duration = Duration.minutes(duration),
    )

    private data class MatchTime(
        val startTime: OffsetDateTime,
        val endTime: OffsetDateTime,
        val duration: Duration,
    )
}

fun Any.loadMatchReportFile(fileName: String): MatchReport {
    val pathName = javaClass.classLoader?.getResource(fileName)?.file ?: error("No such file: $fileName")
    val content = File(pathName).readText()
    val response = Json { ignoreUnknownKeys = true }.decodeFromString<MatchResponse>(content)
    return MatchResponseToMatchReportMapper().map(response)
}