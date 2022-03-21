package com.kamilh.models.api.tour

import com.kamilh.models.Tour
import com.kamilh.models.api.ResponseMapper
import com.kamilh.models.api.league.LeagueMapper

class TourMapper(private val leagueMapper: LeagueMapper) : ResponseMapper<Tour, TourResponse> {

    override fun to(from: Tour): TourResponse =
        TourResponse(
            id = from.id,
            name = from.name,
            season = from.season,
            league = leagueMapper.to(from.league),
            startDate = from.startDate,
            endDate = from.endDate,
            updatedAt = from.updatedAt,
        )

    override fun from(from: TourResponse): Tour =
        Tour(
            id = from.id,
            name = from.name,
            season = from.season,
            league = leagueMapper.from(from.league),
            startDate = from.startDate,
            endDate = from.endDate,
            updatedAt = from.updatedAt,
        )
}