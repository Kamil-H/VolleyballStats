package com.kamilh.volleyballstats.presentation.features.stats.properties

import com.kamilh.volleyballstats.presentation.Resources
import com.kamilh.volleyballstats.presentation.features.common.Property

enum class SetProperty(
    override val shortName: String,
    override val additionalName: String? = null,
    override val description: String,
    override val mandatory: Boolean = false,
    override val filterable: Boolean = true,
) : Property<String> {
    Index(
        shortName = Resources.string.set_index_short_name,
        description = Resources.string.set_index_description,
        mandatory = true,
        filterable = false,
    ),
    Name(
        shortName = Resources.string.set_name_short_name,
        description = Resources.string.set_name_description,
        mandatory = true,
        filterable = false,
    ),
    TeamName(
        shortName = Resources.string.set_team_name_short_name,
        description = Resources.string.set_team_name_description,
        filterable = false,
    ),
    FullTeamName(
        shortName = Resources.string.set_full_team_name_short_name,
        description = Resources.string.set_full_team_name_description,
        filterable = false,
    ),
    Specialization(
        shortName = Resources.string.set_specialization_short_name,
        description = Resources.string.set_specialization_description,
        filterable = false,
    ),
    Attempts(
        shortName = Resources.string.set_attempts_short_name,
        description = Resources.string.set_attempts_description,
    ),
    Perfect(
        shortName = Resources.string.set_perfect_short_name,
        additionalName = Resources.string.set_perfect_additional_name,
        description = Resources.string.set_perfect_description,
    ),
    PerfectPositive(
        shortName = Resources.string.set_perfect_positive_short_name,
        additionalName = Resources.string.set_perfect_positive_additional_name,
        description = Resources.string.set_perfect_positive_description,
    ),
    Efficiency(
        shortName = Resources.string.set_efficiency_short_name,
        additionalName = Resources.string.set_efficiency_additional_name,
        description = Resources.string.set_efficiency_description,
    ),
    Errors(
        shortName = Resources.string.set_errors_short_name,
        additionalName = Resources.string.set_errors_additional_name,
        description = Resources.string.set_errors_description,
    ),
    ErrorsPercent(
        shortName = Resources.string.set_errors_percent_short_name,
        additionalName = Resources.string.set_errors_percent_additional_name,
        description = Resources.string.set_errors_percent_description,
    ),
    PointWinPercent(
        shortName = Resources.string.set_point_win_percent_short_name,
        additionalName = Resources.string.set_point_win_percent_additional_name,
        description = Resources.string.set_point_win_percent_description,
    );

    override val id: String
        get() = this.name
}
