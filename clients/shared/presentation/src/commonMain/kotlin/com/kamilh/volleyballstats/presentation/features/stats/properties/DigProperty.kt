package com.kamilh.volleyballstats.presentation.features.stats.properties

import com.kamilh.volleyballstats.presentation.features.common.Property

enum class DigProperty(
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
    Digs(
        shortName = "Kill",
        description = "",
    ),
    SuccessPercent(
        shortName = "Kill",
        additionalName = "%",
        description = "",
    ),
    Errors(
        shortName = "Reb",
        description = "",
    ),
    PointWinPercent(
        shortName = "Reb",
        additionalName = "%",
        description = "",
    );

    override val id: String
        get() = this.name
}
