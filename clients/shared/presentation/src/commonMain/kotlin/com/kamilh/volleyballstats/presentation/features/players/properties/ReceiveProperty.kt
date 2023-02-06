package com.kamilh.volleyballstats.presentation.features.players.properties

import com.kamilh.volleyballstats.presentation.features.common.Property

enum class ReceiveProperty(
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
    Perfect(
        shortName = "Kill",
        additionalName = "%",
        description = "",
    ),
    PerfectPositive(
        shortName = "Eff",
        additionalName = "%",
        description = "",
    ),
    Efficiency(
        shortName = "Err",
        additionalName = "%",
        description = "",
    ),
    Errors(
        shortName = "Win",
        additionalName = "%",
        description = "",
    ),
    ErrorsPercent(
        shortName = "BP",
        additionalName = "Kill%",
        description = "",
    ),
    SideOut(
        shortName = "BP",
        additionalName = "Eff%",
        description = "",
    ),
    PointWinPercent(
        shortName = "BP",
        additionalName = "Err%",
        description = "",
    );

    override val id: String
        get() = this.name
}
