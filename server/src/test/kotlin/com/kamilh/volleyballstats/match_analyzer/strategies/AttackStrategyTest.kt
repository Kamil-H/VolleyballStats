package com.kamilh.volleyballstats.match_analyzer.strategies

import com.kamilh.volleyballstats.match_analyzer.strategies.AttackStrategy
import com.kamilh.volleyballstats.models.*
import org.junit.Test

class AttackStrategyTest {

    private val strategy = AttackStrategy()

    @Test
    fun `test that sideOut is false when Attack is after a Serve but both players are from the same team`() {
        // GIVEN
        val team = TeamId(0)
        val skills = listOf(Skill.Serve to team, Skill.Attack to team)
        val analysisInput = analysisInputOf(
            plays = skills.map {
                analysisInputPlayOf(
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
                analysisInputPlayOf(
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
                analysisInputPlayOf(
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
                analysisInputPlayOf(
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
                analysisInputPlayOf(skill = it)
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
                analysisInputPlayOf(
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
                analysisInputPlayOf(
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
                analysisInputPlayOf(skill = it)
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
                analysisInputPlayOf(
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
                analysisInputPlayOf(
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
                analysisInputPlayOf(skill = it)
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
                analysisInputPlayOf(skill = it)
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
                analysisInputPlayOf(skill = it)
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
        val skills = listOf(Skill.Serve, Skill.Attack, Skill.Block)
        val effects = listOf(Effect.Positive, Effect.Perfect, Effect.Negative)
        val analysisInput = analysisInputOf(
            plays = skills.mapIndexed { index, skill ->
                analysisInputPlayOf(
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
        val skills = listOf(Skill.Serve, Skill.Attack, Skill.Dig)
        val effects = listOf(Effect.Positive, Effect.Perfect, Effect.Negative)
        val analysisInput = analysisInputOf(
            plays = skills.mapIndexed { index, skill ->
                analysisInputPlayOf(
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