package com.kamilh.volleyballstats.match_analyzer

import com.kamilh.volleyballstats.match_analyzer.strategies.sideOutPlays
import com.kamilh.volleyballstats.models.Skill
import com.kamilh.volleyballstats.models.TeamId
import com.kamilh.volleyballstats.models.analysisInputOf
import com.kamilh.volleyballstats.models.analysisInputPlayOf
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
}