package com.kamilh.volleyballstats.presentation.features.stats.properties

import com.kamilh.volleyballstats.presentation.Resources
import com.kamilh.volleyballstats.presentation.features.common.Property

enum class BlockProperty(
    override val shortName: String,
    override val additionalName: String? = null,
    override val description: String,
    override val mandatory: Boolean = false,
    override val filterable: Boolean = true,
) : Property<String> {

    Index(
        shortName = Resources.string.block_index_short_name,
        description = Resources.string.block_index_description,
        mandatory = true,
        filterable = false,
    ),
    Name(
        shortName = Resources.string.block_name_short_name,
        description = Resources.string.block_name_description,
        mandatory = true,
        filterable = false,
    ),
    TeamName(
        shortName = Resources.string.block_team_name_short_name,
        description = Resources.string.block_team_name_description,
        filterable = false,
    ),
    FullTeamName(
        shortName = Resources.string.block_full_team_name_short_name,
        description = Resources.string.block_full_team_name_description,
        filterable = false,
    ),
    Specialization(
        shortName = Resources.string.block_specialization_short_name,
        description = Resources.string.block_specialization_description,
        filterable = false,
    ),
    Attempts(
        shortName = Resources.string.block_attempts_short_name,
        description = Resources.string.block_attempts_description,
    ),
    Kill(
        shortName = Resources.string.block_kill_short_name,
        description = Resources.string.block_kill_description,
    ),
    KillPerAttempt(
        shortName = Resources.string.block_kill_per_attempt_short_name,
        additionalName = Resources.string.block_kill_per_attempt_additional_name,
        description = Resources.string.block_kill_per_attempt_description,
    ),
    Rebound(
        shortName = Resources.string.block_rebound_short_name,
        description = Resources.string.block_rebound_description,
    ),
    ReboundPerAttempt(
        shortName = Resources.string.block_rebound_per_attempt_short_name,
        additionalName = Resources.string.block_rebound_per_attempt_additional_name,
        description = Resources.string.block_rebound_per_attempt_description,
    ),
    KillPlusRebound(
        shortName = Resources.string.block_kill_plus_rebound_short_name,
        additionalName = Resources.string.block_kill_plus_rebound_additional_name,
        description = Resources.string.block_kill_plus_rebound_description,
    ),
    KillPlusReboundPerAttempt(
        shortName = Resources.string.block_kill_plus_rebound_per_attempt_short_name,
        additionalName = Resources.string.block_kill_plus_rebound_per_attempt_additional_name,
        description = Resources.string.block_kill_plus_rebound_per_attempt_description,
    ),
    Error(
        shortName = Resources.string.block_error_short_name,
        description = Resources.string.block_error_description,
    ),
    ErrorPerAttempt(
        shortName = Resources.string.block_error_per_attempt_short_name,
        additionalName = Resources.string.block_error_per_attempt_additional_name,
        description = Resources.string.block_error_per_attempt_description,
    ),
    PointWinPercent(
        shortName = Resources.string.block_point_win_percent_short_name,
        additionalName = Resources.string.block_point_win_percent_additional_name,
        description = Resources.string.block_point_win_percent_description,
    );

    override val id: String
        get() = this.name
}
