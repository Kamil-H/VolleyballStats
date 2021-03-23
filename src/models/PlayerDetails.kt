package com.kamilh.models

import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
<div class="row">
    <div class="col-sm-4 col-md-4 col-lg-3 col-sm-offset-2 col-lg-offset-3"><div class="datainfo small">Data urodzenia:<span> 06.05.1992</span></div></div>
    <div class="col-sm-5 col-md-4"><div class="datainfo small">Specjalność: <span>Atakujący</span></div></div>
</div>
(...)
<div class="row">
    <div class="col-sm-3 col-md-3 col-lg-3 col-lg-offset-1"><div class="datainfo text-center">Wzrost:<span> 198</span></div></div>
    <div class="col-sm-3 col-md-3 col-lg-3"><div class="datainfo text-center">Waga:<span> 93</span></div></div>
    <div class="col-sm-6 col-md-6 col-lg-4"><div class="datainfo text-center">Zasięg z wyskoku do ataku:<span> 349</span></div></div>
</div>
(...)
<div class="playernumber">Numer<span>6</span></div>
 */

private fun parse(html: String) {
    val document = Jsoup.parse(html)
    var date: LocalDate? = null
    var height: Int? = null
    var weight: Int? = null
    var range: Int? = null
    var number: Int? = null
    document.getElementsByClass("datainfo small").forEach {
        it.children().forEach { child ->
            val regex = Regex("\\d{2}.\\d{2}.\\d{4}")
            regex.find(child.outerHtml())?.value?.let { dateString ->
                try {
                    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    date = LocalDate.parse(dateString, formatter)
                } catch (e: Exception) {
                    println(e)
                }
            }
        }
    }
    val regex = Regex("(\\d+)")
    document.getElementsByClass("datainfo text-center").forEach {
        val childText = it.outerHtml()
        val value = regex.find(childText)?.value?.toIntOrNull()
        if (value != null) {
            when {
                childText.contains("wzrost", ignoreCase = true) -> height = value
                childText.contains("waga", ignoreCase = true) -> weight = value
                childText.contains("zasięg", ignoreCase = true) -> range = value
            }
        }
    }
    document.getElementsByClass("playernumber").forEach {
        number = regex.find(it.outerHtml())?.value?.toIntOrNull()
    }
    println("date: $date, number: $number, height: $height, weight: $weight, range: $range")
}

data class PlayerDetails(
    val date: LocalDate,
    val height: Int,
    val weight: Int,
    val range: Int,
    val number: Int,
)