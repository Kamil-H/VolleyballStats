package com.kamilh.volleyballstats.presentation.features.stats.properties

import com.kamilh.volleyballstats.presentation.Resources
import com.kamilh.volleyballstats.presentation.features.common.Property

enum class ServeProperty(
    override val shortName: String,
    override val additionalName: String? = null,
    override val description: String,
    override val mandatory: Boolean = false,
    override val filterable: Boolean = true,
) : Property<String> {
    Index(
        shortName = Resources.string.serve_index_short_name,
        description = Resources.string.serve_index_description,
        mandatory = true,
        filterable = false,
    ),
    Name(
        shortName = Resources.string.serve_name_short_name,
        description = Resources.string.serve_name_description,
        mandatory = true,
        filterable = false,
    ),
    TeamName(
        shortName = Resources.string.serve_team_name_short_name,
        description = Resources.string.serve_team_name_description,
        filterable = false,
    ),
    FullTeamName(
        shortName = Resources.string.serve_full_team_name_short_name,
        description = Resources.string.serve_full_team_name_description,
        filterable = false,
    ),
    Specialization(
        shortName = Resources.string.serve_specialization_short_name,
        description = Resources.string.serve_specialization_description,
        filterable = false,
    ),
    Attempts(
        shortName = Resources.string.serve_attempts_short_name,
        description = Resources.string.serve_attempts_description,
    ),
    Efficiency(
        shortName = Resources.string.serve_efficiency_short_name,
        additionalName = Resources.string.serve_efficiency_additional_name,
        description = Resources.string.serve_efficiency_description,
    ),
    Ace(
        shortName = Resources.string.serve_ace_short_name,
        additionalName = Resources.string.serve_ace_additional_name,
        description = Resources.string.serve_ace_description,
    ),
    AcePercent(
        shortName = Resources.string.serve_ace_percent_short_name,
        additionalName = Resources.string.serve_ace_percent_additional_name,
        description = Resources.string.serve_ace_percent_description,
    ),
    Freeball(
        shortName = Resources.string.serve_freeball_short_name,
        additionalName = Resources.string.serve_freeball_additional_name,
        description = Resources.string.serve_freeball_description,
    ),
    FreeballPercent(
        shortName = Resources.string.serve_freeball_percent_short_name,
        additionalName = Resources.string.serve_freeball_percent_additional_name,
        description = Resources.string.serve_freeball_percent_description,
    ),
    AceFreeball(
        shortName = Resources.string.serve_ace_freeball_short_name,
        additionalName = Resources.string.serve_ace_freeball_additional_name,
        description = Resources.string.serve_ace_freeball_description,
    ),
    AceFreeballPercent(
        shortName = Resources.string.serve_ace_freeball_percent_short_name,
        additionalName = Resources.string.serve_ace_freeball_percent_additional_name,
        description = Resources.string.serve_ace_freeball_percent_description,
    ),
    Errors(
        shortName = Resources.string.serve_errors_short_name,
        additionalName = Resources.string.serve_errors_additional_name,
        description = Resources.string.serve_errors_description,
    ),
    ErrorsPercent(
        shortName = Resources.string.serve_errors_percent_short_name,
        additionalName = Resources.string.serve_errors_percent_additional_name,
        description = Resources.string.serve_errors_percent_description,
    ),
    PointWinPercent(
        shortName = Resources.string.serve_point_win_percent_short_name,
        additionalName = Resources.string.serve_point_win_percent_additional_name,
        description = Resources.string.serve_point_win_percent_description,
    );

    override val id: String
        get() = this.name
}
