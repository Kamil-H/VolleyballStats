package com.kamilh.volleyballstats.presentation.features.stats.properties

import com.kamilh.volleyballstats.presentation.features.common.Property

enum class ServeProperty(
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
    Efficiency(
        shortName = "Kill",
        additionalName = "%",
        description = "",
    ),
    Ace(
        shortName = "Eff",
        additionalName = "%",
        description = "",
    ),
    AcePercent(
        shortName = "Err",
        additionalName = "%",
        description = "",
    ),
    Freeball(
        shortName = "Win",
        additionalName = "%",
        description = "",
    ),
    FreeballPercent(
        shortName = "BP",
        additionalName = "Kill%",
        description = "",
    ),
    AceFreeball(
        shortName = "BP",
        additionalName = "Eff%",
        description = "",
    ),
    AceFreeballPercent(
        shortName = "BP",
        additionalName = "Err%",
        description = "",
    ),
    Errors(
        shortName = "BP",
        additionalName = "Eff%",
        description = "",
    ),
    ErrorsPercent(
        shortName = "BP",
        additionalName = "Eff%",
        description = "",
    ),
    PointWinPercent(
        shortName = "BP",
        additionalName = "Eff%",
        description = "",
    );

    override val id: String
        get() = this.name
}
