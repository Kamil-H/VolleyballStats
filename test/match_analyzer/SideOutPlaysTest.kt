package com.kamilh.match_analyzer

import com.kamilh.match_analyzer.strategies.sideOutPlays
import com.kamilh.models.Skill
import com.kamilh.models.TeamId
import com.kamilh.models.analysisInputOf
import com.kamilh.models.analysisInputPlayOf
import org.junit.Test

class SideOutPlaysTest {

    @Test
    fun `test that sideOutPlays contains all of the plays after Serve until there is a play from other team`() {
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
        val input = analysisInputOf(
            plays = skills.map {
                analysisInputPlayOf(
                    skill = it.first,
                    team = it.second,
                )
            }
        )

        // WHEN
        val sideOutPlays = input.sideOutPlays()

        // THEN
        assert(sideOutPlays.size == 3)
        assert(sideOutPlays[0] == input.plays[1])
        assert(sideOutPlays[1] == input.plays[2])
        assert(sideOutPlays[2] == input.plays[3])
    }

    @Test
    fun `test that sideOutPlays contains only plays after Serve until there is a play from other team`() {
        // GIVEN
        val firstTeam = TeamId(0)
        val secondTeam = TeamId(1)
        val skills = listOf(
            Skill.Serve to firstTeam,
            Skill.Receive to secondTeam,
            Skill.Set to secondTeam,
            Skill.Attack to secondTeam,
            Skill.Block to firstTeam,
            Skill.Receive to secondTeam,
            Skill.Set to secondTeam,
            Skill.Attack to secondTeam,
        )
        val input = analysisInputOf(
            plays = skills.map {
                analysisInputPlayOf(
                    skill = it.first,
                    team = it.second,
                )
            }
        )

        // WHEN
        val sideOutPlays = input.sideOutPlays()

        // THEN
        assert(sideOutPlays.size == 3)
        assert(sideOutPlays[0] == input.plays[1])
        assert(sideOutPlays[1] == input.plays[2])
        assert(sideOutPlays[2] == input.plays[3])
    }

    @Test
    fun `test that sideOutPlays is empty when there was only a Serve pla`() {
        // GIVEN
        val skills = listOf(Skill.Serve)
        val input = analysisInputOf(
            plays = skills.map {
                analysisInputPlayOf(skill = it)
            }
        )

        // WHEN
        val sideOutPlays = input.sideOutPlays()

        // THEN
        assert(sideOutPlays.isEmpty())
    }

    @Test(expected = IllegalStateException::class)
    fun `test that there is exception thrown then plays list is empty`() {
        // GIVEN
        val input = analysisInputOf(plays = emptyList())

        // WHEN
        input.sideOutPlays()
    }

    @Test(expected = IllegalStateException::class)
    fun `test that there is exception thrown then plays doesn't start from Serve`() {
        // GIVEN
        val input = analysisInputOf(plays = listOf(analysisInputPlayOf(skill = Skill.Receive)))

        // WHEN
        input.sideOutPlays()
    }
}