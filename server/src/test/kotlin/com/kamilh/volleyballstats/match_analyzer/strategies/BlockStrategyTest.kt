package com.kamilh.volleyballstats.match_analyzer.strategies

import com.kamilh.volleyballstats.domain.models.PlayerId
import com.kamilh.volleyballstats.domain.models.Skill
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.models.analysisInputOf
import com.kamilh.volleyballstats.models.analysisInputPlayOf
import org.junit.Test

class BlockStrategyTest {

    private val strategy = BlockStrategy()

    @Test
    fun `test that attackerId is equal to the PlayerId that belongs to the player that attacked`() {
        // GIVEN
        val firstPlayer = PlayerId(0)
        val secondPlayer = PlayerId(1)
        val skills = listOf(
            Skill.Serve to secondPlayer,
            Skill.Attack to firstPlayer,
            Skill.Block to secondPlayer,
        )

        val analysisInput = analysisInputOf(
            plays = skills.map {
                analysisInputPlayOf(
                    skill = it.first,
                    player = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().attackerId == firstPlayer)
    }

    @Test
    fun `test that setterId is equal to the PlayerId that belongs to the player that set`() {
        // GIVEN
        val firstPlayer = PlayerId(0)
        val secondPlayer = PlayerId(1)
        val skills = listOf(
            Skill.Serve to firstPlayer,
            Skill.Set to firstPlayer,
            Skill.Attack to secondPlayer,
            Skill.Block to firstPlayer,
        )

        val analysisInput = analysisInputOf(
            plays = skills.map {
                analysisInputPlayOf(
                    skill = it.first,
                    player = it.second,
                )
            }
        )

        // WHEN
        val actions = strategy.check(analysisInput)

        // THEN
        assert(actions.first().setterId == firstPlayer)
    }

    @Test
    fun `test that afterSideOut is true when attack happened in side out action`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(
            Skill.Serve to firstTeam,
            Skill.Receive to secondTeam,
            Skill.Set to secondTeam,
            Skill.Attack to secondTeam,
            Skill.Block to firstTeam,
        )

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
        assert(actions.first().afterSideOut)
    }

    @Test
    fun `test that afterSideOut is false when attack happened not in side out action`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(
            Skill.Serve to firstTeam,
            Skill.Receive to secondTeam,
            Skill.Set to secondTeam,
            Skill.Attack to secondTeam,
            Skill.Dig to firstTeam,
            Skill.Attack to firstTeam,
            Skill.Block to secondTeam,
        )

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
        assert(!actions.first().afterSideOut)
    }
}