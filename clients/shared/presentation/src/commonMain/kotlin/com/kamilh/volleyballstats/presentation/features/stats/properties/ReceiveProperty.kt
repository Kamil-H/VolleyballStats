package com.kamilh.volleyballstats.presentation.features.stats.properties

import com.kamilh.volleyballstats.presentation.Resources
import com.kamilh.volleyballstats.presentation.features.common.Property

enum class ReceiveProperty(
    override val shortName: String,
    override val additionalName: String? = null,
    override val description: String,
    override val mandatory: Boolean = false,
    override val filterable: Boolean = true,
) : Property<String> {
    Index(
        shortName = Resources.string.receive_index_short_name,
        description = Resources.string.receive_index_description,
        mandatory = true,
        filterable = false,
    ),
    Name(
        shortName = Resources.string.receive_name_short_name,
        description = Resources.string.receive_name_description,
        mandatory = true,
        filterable = false,
    ),
    TeamName(
        shortName = Resources.string.receive_team_name_short_name,
        description = Resources.string.receive_team_name_description,
        filterable = false,
    ),
    FullTeamName(
        shortName = Resources.string.receive_full_team_name_short_name,
        description = Resources.string.receive_full_team_name_description,
        filterable = false,
    ),
    Specialization(
        shortName = Resources.string.receive_specialization_short_name,
        description = Resources.string.receive_specialization_description,
        filterable = false,
    ),
    Attempts(
        shortName = Resources.string.receive_attempts_short_name,
        description = Resources.string.receive_attempts_description,
    ),
    Perfect(
        shortName = Resources.string.receive_perfect_short_name,
        additionalName = Resources.string.receive_perfect_additional_name,
        description = Resources.string.receive_perfect_description,
    ),
    PerfectPositive(
        shortName = Resources.string.receive_perfect_positive_short_name,
        additionalName = Resources.string.receive_perfect_positive_additional_name,
        description = Resources.string.receive_perfect_positive_description,
    ),
    Efficiency(
        shortName = Resources.string.receive_efficiency_short_name,
        additionalName = Resources.string.receive_efficiency_additional_name,
        description = Resources.string.receive_efficiency_description,
    ),
    Errors(
        shortName = Resources.string.receive_errors_short_name,
        additionalName = Resources.string.receive_errors_additional_name,
        description = Resources.string.receive_errors_description,
    ),
    ErrorsPercent(
        shortName = Resources.string.receive_errors_percent_short_name,
        additionalName = Resources.string.receive_errors_percent_additional_name,
        description = Resources.string.receive_errors_percent_description,
    ),
    SideOut(
        shortName = Resources.string.receive_side_out_short_name,
        additionalName = Resources.string.receive_side_out_additional_name,
        description = Resources.string.receive_side_out_description,
    ),
    PointWinPercent(
        shortName = Resources.string.receive_point_win_percent_short_name,
        additionalName = Resources.string.receive_point_win_percent_additional_name,
        description = Resources.string.receive_point_win_percent_description,
    );

    override val id: String
        get() = this.name
}
