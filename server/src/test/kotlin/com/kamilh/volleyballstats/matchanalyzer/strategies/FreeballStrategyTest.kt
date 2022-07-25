package com.kamilh.volleyballstats.matchanalyzer.strategies

import com.kamilh.volleyballstats.domain.models.Skill
import com.kamilh.volleyballstats.domain.models.TeamId
import com.kamilh.volleyballstats.models.analysisInputOf
import com.kamilh.volleyballstats.models.analysisInputPlayOf
import org.junit.Test

class FreeballStrategyTest {

    private val strategy = FreeballStrategy()

    @Test
    fun `test that afterSideOut is true when freeball happened after side out action`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(
            Skill.Serve to firstTeam,
            Skill.Receive to secondTeam,
            Skill.Set to secondTeam,
            Skill.Attack to secondTeam,
            Skill.Freeball to firstTeam,
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
    fun `test that afterSideOut is false when set happened not after side out action`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(
            Skill.Serve to firstTeam,
            Skill.Receive to secondTeam,
            Skill.Set to secondTeam,
            Skill.Attack to secondTeam,
            Skill.Dig to firstTeam,
            Skill.Set to firstTeam,
            Skill.Freeball to secondTeam,
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
        assert(!actions.last().afterSideOut)
    }
}