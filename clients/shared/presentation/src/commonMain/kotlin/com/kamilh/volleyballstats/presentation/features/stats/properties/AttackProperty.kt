package com.kamilh.volleyballstats.presentation.features.stats.properties

import com.kamilh.volleyballstats.presentation.Resources
import com.kamilh.volleyballstats.presentation.features.common.Property

enum class AttackProperty(
    override val shortName: String,
    override val additionalName: String? = null,
    override val description: String,
    override val mandatory: Boolean = false,
    override val filterable: Boolean = true,
) : Property<String> {
    Index(
        shortName = Resources.string.attack_index_short_name,
        description = Resources.string.attack_index_description,
        mandatory = true,
        filterable = false,
    ),
    Name(
        shortName = Resources.string.attack_name_short_name,
        description = Resources.string.attack_name_description,
        mandatory = true,
        filterable = false,
    ),
    TeamName(
        shortName = Resources.string.attack_team_name_short_name,
        description = Resources.string.attack_team_name_description,
        filterable = false,
    ),
    FullTeamName(
        shortName = Resources.string.attack_full_team_name_short_name,
        description = Resources.string.attack_full_team_name_description,
        filterable = false,
    ),
    Specialization(
        shortName = Resources.string.attack_specialization_short_name,
        description = Resources.string.attack_specialization_description,
        filterable = false,
    ),
    Attempts(
        shortName = Resources.string.attack_attempts_short_name,
        description = Resources.string.attack_attempts_description,
    ),
    Kill(
        shortName = Resources.string.attack_kill_short_name,
        additionalName = Resources.string.attack_kill_additional_name,
        description = Resources.string.attack_kill_description,
    ),
    Efficiency(
        shortName = Resources.string.attack_efficiency_short_name,
        additionalName = Resources.string.attack_efficiency_additional_name,
        description = Resources.string.attack_efficiency_description,
    ),
    Error(
        shortName = Resources.string.attack_error_short_name,
        additionalName = Resources.string.attack_error_additional_name,
        description = Resources.string.attack_error_description,
    ),
    PointWinPercent(
        shortName = Resources.string.attack_point_win_percent_short_name,
        additionalName = Resources.string.attack_point_win_percent_additional_name,
        description = Resources.string.attack_point_win_percent_description,
    ),
    KillBreakPoint(
        shortName = Resources.string.attack_kill_break_point_short_name,
        additionalName = Resources.string.attack_kill_break_point_additional_name,
        description = Resources.string.attack_kill_break_point_description,
    ),
    EfficiencyBreakPoint(
        shortName = Resources.string.attack_efficiency_break_point_short_name,
        additionalName = Resources.string.attack_efficiency_break_point_additional_name,
        description = Resources.string.attack_efficiency_break_point_description,
    ),
    ErrorBreakPoint(
        shortName = Resources.string.attack_error_break_point_short_name,
        additionalName = Resources.string.attack_error_break_point_additional_name,
        description = Resources.string.attack_error_break_point_description,
    ),
    KillSideOut(
        shortName = Resources.string.attack_kill_side_out_short_name,
        additionalName = Resources.string.attack_kill_side_out_additional_name,
        description = Resources.string.attack_kill_side_out_description,
    ),
    EfficiencySideOut(
        shortName = Resources.string.attack_efficiency_side_out_short_name,
        additionalName = Resources.string.attack_efficiency_side_out_additional_name,
        description = Resources.string.attack_efficiency_side_out_description,
    ),
    ErrorSideOut(
        shortName = Resources.string.attack_error_side_out_short_name,
        additionalName = Resources.string.attack_error_side_out_additional_name,
        description = Resources.string.attack_error_side_out_description,
    );

    override val id: String
        get() = this.name
}
