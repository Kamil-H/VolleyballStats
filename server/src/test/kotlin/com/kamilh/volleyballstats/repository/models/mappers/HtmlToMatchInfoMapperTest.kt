package com.kamilh.volleyballstats.repository.models.mappers

import com.kamilh.volleyballstats.domain.assertSuccess
import com.kamilh.volleyballstats.domain.models.MatchId
import com.kamilh.volleyballstats.domain.models.MatchInfo
import com.kamilh.volleyballstats.domain.models.Result
import com.kamilh.volleyballstats.repository.extensions.toPolishLeagueLocalDate
import com.kamilh.volleyballstats.repository.parsing.JsoupHtmlParser
import org.junit.Test

class HtmlToMatchInfoMapperTest {

    private val mapper = HtmlToAllMatchesItemMapper(htmlParser = JsoupHtmlParser())
    private val dateString = "03.06.2021, 16:17"

    @Test
    fun `test if when green is 3 then result is PotentiallyFinished and id is parsed properly`() {
        // GIVEN
        val idString = "1101312"

        val html = html(
            dateString = dateString,
            idString = idString,
            green = "3",
        )

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.first()
        require(first is MatchInfo.PotentiallyFinished)
        assert(first.id == MatchId(idString.toLong()))
    }

    @Test
    fun `test if when date is from in future result is Scheduled and id is parsed properly`() {
        // GIVEN
        val date = dateString.toPolishLeagueLocalDate()!!.atPolandZone()
        val idString = "1101312"

        val html = html(
            dateString = dateString,
            idString = idString,
            green = "1",
        )

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.first()
        require(first is MatchInfo.Scheduled)
        assert(first.date == date)
        assert(first.id == MatchId(idString.toLong()))
    }

    @Test
    fun `test if when date is from in midnight result is NotScheduled and id is parsed properly`() {
        // GIVEN
        val dateString = "03.06.2021, 00:00"
        val idString = "1101312"

        val html = html(
            dateString = dateString,
            idString = idString,
            green = "1",
        )

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Success)
        val first = mapped.value.first()
        require(first is MatchInfo.NotScheduled)
        assert(first.id == MatchId(idString.toLong()))
    }

    @Test
    fun `test if when dateString is empty then result is Failure`() {
        // GIVEN
        val idString = "1101312"

        val html = html(
            dateString = "",
            idString = idString,
            green = "1",
        )

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `test if when dateString is not properly formatted then result is Failure`() {
        // GIVEN
        val idString = "1101312"

        val html = html(
            dateString = "2021-06-03",
            idString = idString,
            green = "1",
        )

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `test if when idString is empty then result is Failure`() {
        // GIVEN
        val idString = ""

        val html = html(
            dateString = dateString,
            idString = idString,
            green = "1",
        )

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `test if when idString is not a number then result is Failure`() {
        // GIVEN
        val idString = "text"

        val html = html(
            dateString = dateString,
            idString = idString,
            green = "1",
        )

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        require(mapped is Result.Failure)
    }

    @Test
    fun `empty list returned when parsing html without TeamIds`() {
        // GIVEN
        val html = noTeamsHtml

        // WHEN
        val mapped = mapper.map(html)

        // THEN
        mapped.assertSuccess {
            assert(this.isEmpty())
        }
    }
}

private fun html(dateString: String, idString: String, green: String): String =
    """
<div class="hr small">&nbsp;</div></div><div class="gameData team-30288 team-1405"><div class="row text-center gridtable games alter" data-type="mecz" data-id="1">

    <div class="col-xs-2  col-sm-2 col-md-2 col-lg-2 tablecell text-left">
        <div class="date khanded"><span class="tvico">TV</span>&nbsp; $dateString</div>
    </div>						
    <div class="hidden-xs col-sm-1 col-md-1 col-lg-1  tablecell "><a href='/teams/id/30288/tour/2020.html'><img src="https://dl.siatkarskaliga.pl/412889/inline/scalecrop=100x100/d377f7/aluron_cmc_logo_2020.png" class="img-responsive" alt="" /></a></div>
    <div class="col-xs-3  col-sm-2 col-md-2 col-lg-2  tablecell ">
        <h2><a href='/teams/id/30288/tour/2020.html'>Aluron CMC Warta Zawiercie</a></h2>
    </div>
    <div class="col-xs-2  col-sm-2 col-md-2 col-lg-2  tablecell  gameresultcontainer ">
        <div class="gameresult clickable" onclick="location.href='/games/id/$idString/tour/2020.html';">
            <span class="red">1</span><span class="doubledot">:</span><span class="green">$green</span>
        </div>
                                
    </div>						
    <div class="col-xs-3  col-sm-2 col-md-2 col-lg-2  tablecell ">
        <h2><a href='/teams/id/1405/tour/2020.html'>Jastrzębski Węgiel</a></h2>
    </div>
    <div class="hidden-xs col-sm-1 col-md-1 col-lg-1  tablecell "><a href='/teams/id/1405/tour/2020.html'><img src="https://dl.siatkarskaliga.pl/411001/inline/scalecrop=100x100/d565a1/Jastrzebski-Wegiel_logotyp.png" class="img-responsive" alt="" /></a></div>
    <div class="col-xs-2  col-sm-2 col-md-2 col-lg-2  tablecell text-right">
        <a class="btn btn-default btm-margins" href="/games/id/1101312/tour/2020.html">więcej</a>
    </div>
    
</div>
        """.trimIndent()

private val noTeamsHtml: String =
    """
<div class="row text-center gridtable games alter" data-type="mecz" data-id="0"> 
 <div class="col-xs-2  col-sm-2 col-md-2 col-lg-2 tablecell text-left"> 
  <div class="date khanded"></div> 
 </div> 
 <div class="hidden-xs col-sm-1 col-md-1 col-lg-1  tablecell ">
  <a href="#" onclick="javascript: return false;"><img src="//img.siatkarskaliga.pl/placehold/nieznane_logo_100.png" class="img-responsive" alt=""></a>
 </div> 
 <div class="col-xs-3  col-sm-2 col-md-2 col-lg-2  tablecell "> 
  <h2><a href="#" onclick="javascript: return false;">1dr.FZ</a></h2> 
 </div> 
 <div class="col-xs-2  col-sm-2 col-md-2 col-lg-2  tablecell  gameresultcontainer "> 
  <div class="gameresult clickable" onclick="location.href='/games/id/1102022/tour/2021.html';"> <span class="">0</span><span class="doubledot">:</span><span class="">0</span> 
  </div> 
 </div> 
 <div class="col-xs-3  col-sm-2 col-md-2 col-lg-2  tablecell "> 
  <h2><a href="#" onclick="javascript: return false;">8dr.FZ</a></h2> 
 </div> 
 <div class="hidden-xs col-sm-1 col-md-1 col-lg-1  tablecell ">
  <a href="#" onclick="javascript: return false;"><img src="//img.siatkarskaliga.pl/placehold/nieznane_logo_100.png" class="img-responsive" alt=""></a>
 </div> 
 <div class="col-xs-2  col-sm-2 col-md-2 col-lg-2  tablecell text-right"> <a class="btn btn-default btm-margins" href="/games/id/1102022/tour/2021.html">więcej</a> 
 </div> 
</div>
    """.trimIndent()