package match_analyzer.strategies

import com.kamilh.match_analyzer.AnalysisInput
import com.kamilh.match_analyzer.strategies.AttackStrategy
import com.kamilh.models.*
import org.junit.Test
import java.time.LocalDateTime
import java.util.*

class AttackStrategyTest {

    private val strategy = AttackStrategy()

    @Test
    fun `test that sideOut is false when Attack is after a Serve but both players are from the same team`() {
        // GIVEN
        val team = TeamId(0)
        val skills = listOf(Skill.Serve to team, Skill.Attack to team)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                playOf(
                    skill = it.first,
                    team = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(!actions.first().sideOut)
    }

    @Test
    fun `test that sideOut is true when Attack is after a Serve, Receive and Set`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(Skill.Serve to firstTeam, Skill.Receive to secondTeam, Skill.Set to secondTeam, Skill.Attack to secondTeam)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                playOf(
                    skill = it.first,
                    team = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().sideOut)
    }

    @Test
    fun `test that sideOut is true when Attack is after a Serve and Receive`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(Skill.Serve to firstTeam, Skill.Receive to secondTeam, Skill.Attack to secondTeam)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                playOf(
                    skill = it.first,
                    team = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().sideOut)
    }

    @Test
    fun `test that sideOut is true when Attack is after a Serve`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(Skill.Serve to firstTeam, Skill.Attack to secondTeam)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                playOf(
                    skill = it.first,
                    team = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().sideOut)
    }

    @Test
    fun `test that sideOut is false for any other Attack after the first one`() {
        // GIVEN
        val skills = listOf(Skill.Serve, Skill.Attack, Skill.Set, Skill.Attack)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                playOf(skill = it)
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(!actions.last().sideOut)
    }

    @Test
    fun `test that receiveEffect is not null when Attack is after a Serve, Receive and Set`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(Skill.Serve to firstTeam, Skill.Receive to secondTeam, Skill.Set to secondTeam, Skill.Attack to secondTeam)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                playOf(
                    skill = it.first,
                    team = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().receiveEffect != null)
    }

    @Test
    fun `test that receiveEffect is not null when Attack is after a Serve and Receive`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(Skill.Serve to firstTeam, Skill.Receive to secondTeam, Skill.Attack to secondTeam)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                playOf(
                    skill = it.first,
                    team = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().receiveEffect != null)
    }

    @Test
    fun `test that receiveEffect is null for any other Attack after the first one`() {
        // GIVEN
        val skills = listOf(Skill.Serve, Skill.Attack, Skill.Set, Skill.Attack)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                playOf(skill = it)
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.last().receiveEffect == null)
    }

    @Test
    fun `test that receiverId is not null when Attack is after a Serve, Receive and Set`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(Skill.Serve to firstTeam, Skill.Receive to secondTeam, Skill.Set to secondTeam, Skill.Attack to secondTeam)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                playOf(
                    skill = it.first,
                    team = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().receiverId != null)
    }

    @Test
    fun `test that receiverId is not null when Attack is after a Serve and Receive`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(Skill.Serve to firstTeam, Skill.Receive to secondTeam, Skill.Attack to secondTeam)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                playOf(
                    skill = it.first,
                    team = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().receiverId != null)
    }

    @Test
    fun `test that receiverId is null for any other Attack after the first one`() {
        // GIVEN
        val skills = listOf(Skill.Serve, Skill.Attack, Skill.Set, Skill.Attack)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                playOf(skill = it)
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.last().receiverId == null)
    }

    @Test
    fun `test that setEffect is not null when Attack is after Set`() {
        // GIVEN
        val skills = listOf(Skill.Serve, Skill.Set, Skill.Attack)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                playOf(skill = it)
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().setEffect != null)
    }

    @Test
    fun `test that setterId is not null when Attack is after Set`() {
        // GIVEN
        val skills = listOf(Skill.Serve, Skill.Set, Skill.Attack)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                playOf(skill = it)
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().setEffect != null)
    }

    @Test
    fun `test that blockAttempt is true when Attack was Perfect and next there was a Block`() {
        // GIVEN
        val skills = listOf(Skill.Attack, Skill.Block)
        val effects = listOf(Effect.Perfect, Effect.Negative)
        val analysisInput = analysisInputOf(
            plays = skills.mapIndexed { index, skill ->
                playOf(
                    skill = skill,
                    effect = effects[index]
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.last().blockAttempt)
    }

    @Test
    fun `test that digAttempt is true when Attack was Perfect and next there was a Dig`() {
        // GIVEN
        val skills = listOf(Skill.Attack, Skill.Dig)
        val effects = listOf(Effect.Perfect, Effect.Negative)
        val analysisInput = analysisInputOf(
            plays = skills.mapIndexed { index, skill ->
                playOf(
                    skill = skill,
                    effect = effects[index]
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.last().digAttempt)
    }
}

fun analysisInputOf(
    plays: List<AnalysisInput.Play> = listOf(),
    matchId: MatchReportId = matchReportIdOf(),
    set: Int = 0,
    currentScore: CurrentScore = currentScoreOf(),
    rallyStartTime: LocalDateTime = LocalDateTime.now(),
    rallyEndTime: LocalDateTime = LocalDateTime.now(),
): AnalysisInput = AnalysisInput(
    plays = plays,
    matchId = matchId,
    set = set,
    currentScore = currentScore,
    rallyStartTime = rallyStartTime,
    rallyEndTime = rallyEndTime,
)

fun playOf(
    id: String = UUID.randomUUID().toString(),
    effect: Effect = Effect.Perfect,
    player: PlayerId = playerIdOf(),
    skill: Skill = Skill.Attack,
    team: TeamId = teamIdOf(),
    position: PlayerPosition = PlayerPosition.P1,
): AnalysisInput.Play = AnalysisInput.Play(
    id = id,
    effect = effect,
    player = player,
    skill = skill,
    team = team,
    position = position,
)

fun lineupOf(
    p1: PlayerId = playerIdOf(1),
    p2: PlayerId = playerIdOf(2),
    p3: PlayerId = playerIdOf(3),
    p4: PlayerId = playerIdOf(4),
    p5: PlayerId = playerIdOf(5),
    p6: PlayerId = playerIdOf(6),
): Lineup = Lineup(
    p1 = p1,
    p2 = p2,
    p3 = p3,
    p4 = p4,
    p5 = p5,
    p6 = p6,
)

fun currentScoreOf(
    ownTeam: Int = 0,
    opponentTeam: Int = 0,
): CurrentScore = CurrentScore(
    ownTeam = ownTeam,
    opponentTeam = opponentTeam,
)