package com.kamilh.volleyballstats.repository.models.mappers

import com.kamilh.volleyballstats.models.MatchReportId
import com.kamilh.volleyballstats.repository.parsing.ParseError
import com.kamilh.volleyballstats.repository.parsing.ParseResult
import me.tatarka.inject.annotations.Inject

/**
    <a class="btn btn-default btm-margins" href="https://www.plusliga.pl/games/action/downloadStats/matchId/2103711.html">Statystyki meczu</a>
    (...)
    <iframe class="widget-ppp widget-pbp" src="https://widgets.volleystation.com/app/widget/play-by-play/2103711?home_image=https://dl.siatkarskaliga.pl/410999/inline/scalecrop=250x250/182736/Grupa-Azoty-ZAKSA-K%C4%99dzierzyn-Ko%C5%BAle_logotyp.png&away_image=https://dl.siatkarskaliga.pl/412482/inline/scalecrop=250x250/d8f9c2/slepsk.png&side_force=home"></iframe>
 */
@Inject
class HtmlToMatchReportId : HtmlMapper<MatchReportId> {

    override fun map(html: String): ParseResult<MatchReportId> {
        val playByPlay = Regex("(?<=play-by-play/)\\d+").find(html)?.value?.toLongOrNull()
        if (playByPlay != null) {
            return ParseResult.success(MatchReportId(playByPlay))
        }

        return ParseResult.failure(
            ParseError.Html(
                content = html,
                exception = IllegalStateException("MatchReportId not found")
            )
        )
    }
}