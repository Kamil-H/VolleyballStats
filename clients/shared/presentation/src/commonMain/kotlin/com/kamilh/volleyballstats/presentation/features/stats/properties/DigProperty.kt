package com.kamilh.volleyballstats.presentation.features.stats.properties

import com.kamilh.volleyballstats.presentation.Resources
import com.kamilh.volleyballstats.presentation.features.common.Property

enum class DigProperty(
    override val shortName: String,
    override val additionalName: String? = null,
    override val description: String,
    override val mandatory: Boolean = false,
    override val filterable: Boolean = true,
) : Property<String> {
    Index(
        shortName = Resources.string.dig_index_short_name,
        description = Resources.string.dig_index_description,
        mandatory = true,
        filterable = false,
    ),
    Name(
        shortName = Resources.string.dig_name_short_name,
        description = Resources.string.dig_name_description,
        mandatory = true,
        filterable = false,
    ),
    TeamName(
        shortName = Resources.string.dig_team_name_short_name,
        description = Resources.string.dig_team_name_description,
        filterable = false,
    ),
    FullTeamName(
        shortName = Resources.string.dig_full_team_name_short_name,
        description = Resources.string.dig_full_team_name_description,
        filterable = false,
    ),
    Specialization(
        shortName = Resources.string.dig_specialization_short_name,
        description = Resources.string.dig_specialization_description,
        filterable = false,
    ),
    Attempts(
        shortName = Resources.string.dig_attempts_short_name,
        description = Resources.string.dig_attempts_description,
    ),
    Digs(
        shortName = Resources.string.dig_digs_short_name,
        description = Resources.string.dig_digs_description,
    ),
    SuccessPercent(
        shortName = Resources.string.dig_success_percent_short_name,
        additionalName = Resources.string.dig_success_percent_additional_name,
        description = Resources.string.dig_success_percent_description,
    ),
    Errors(
        shortName = Resources.string.dig_errors_short_name,
        description = Resources.string.dig_errors_description,
    ),
    PointWinPercent(
        shortName = Resources.string.dig_point_win_percent_short_name,
        additionalName = Resources.string.dig_point_win_percent_additional_name,
        description = Resources.string.dig_point_win_percent_description,
    );

    override val id: String
        get() = this.name
}
