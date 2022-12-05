package com.kamilh.volleyballstats.presentation.features.common

import com.kamilh.volleyballstats.domain.models.Url

data class GroupedMatchItem(
    val title: String,
    val items: List<MatchItem>,
)

data class MatchItem(
    val id: Long,
    val left: SideDetails,
    val right: SideDetails,
    val centerText: String,
    val bottomText: TextPair?,
) {

    data class SideDetails(
        val label: String,
        val imageUrl: Url,
    )
}
