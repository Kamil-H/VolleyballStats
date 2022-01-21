package com.kamilh.match_analyzer

import com.kamilh.models.analysisInputOf
import com.kamilh.models.analysisInputPlayOf
import org.junit.Test

class AnalysisInputTest {

    @Test
    fun `test that values are getting mapped properly`() {
        // GIVEN
        val play = analysisInputPlayOf()
        val analysisInput = analysisInputOf(plays = listOf(play))

        // WHEN
        val generalInfo = analysisInput.generalInfo(play)

        // THEN
        assert(generalInfo.playerInfo.playerId == play.player)
        assert(generalInfo.playerInfo.position == play.position)
        assert(generalInfo.playerInfo.teamId == play.team)
        assert(generalInfo.effect == play.effect)
    }
}