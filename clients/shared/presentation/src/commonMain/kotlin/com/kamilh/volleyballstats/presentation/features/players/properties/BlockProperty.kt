package com.kamilh.volleyballstats.presentation.features.players.properties

import com.kamilh.volleyballstats.presentation.features.common.Property

enum class BlockProperty(
    override val shortName: String,
    override val additionalName: String? = null,
    override val description: String,
    override val mandatory: Boolean = false,
    override val filterable: Boolean = true,
) : Property<String> {

    Index(
        shortName = "In.",
        description = "",
        mandatory = true,
        filterable = false,
    ),
    Name(
        shortName = "Name",
        description = "",
        mandatory = true,
        filterable = false,
    ),
    TeamName(
        shortName = "Team",
        description = "",
        filterable = false,
    ),
    FullTeamName(
        shortName = "Team",
        description = "",
        filterable = false,
    ),
    Specialization(
        shortName = "Pos",
        description = "",
        filterable = false,
    ),
    Attempts(
        shortName = "Att",
        description = "",
    ),
    Kill(
        shortName = "Kill",
        description = "",
    ),
    KillPerAttempt(
        shortName = "Kill",
        additionalName = "%",
        description = "",
    ),
    Rebound(
        shortName = "Reb",
        description = "",
    ),
    ReboundPerAttempt(
        shortName = "Reb",
        additionalName = "%",
        description = "",
    ),
    KillPlusRebound(
        shortName = "Kill+",
        additionalName = "Reb",
        description = "",
    ),
    KillPlusReboundPerAttempt(
        shortName = "Kill+",
        additionalName = "Reb%",
        description = "",
    ),
    Error(
        shortName = "Err",
        description = "",
    ),
    ErrorPerAttempt(
        shortName = "Err",
        description = "%",
    ),
    PointWinPercent(
        shortName = "Win",
        additionalName = "%",
        description = "",
    );

    override val id: String
        get() = this.name
}
