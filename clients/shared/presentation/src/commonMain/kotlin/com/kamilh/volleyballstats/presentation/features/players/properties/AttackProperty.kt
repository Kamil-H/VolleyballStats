package com.kamilh.volleyballstats.presentation.features.players.properties

import com.kamilh.volleyballstats.presentation.features.common.Property

enum class AttackProperty(
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
        additionalName = "%",
        description = "",
    ),
    Efficiency(
        shortName = "Eff",
        additionalName = "%",
        description = "",
    ),
    Error(
        shortName = "Err",
        additionalName = "%",
        description = "",
    ),
    PointWinPercent(
        shortName = "Win",
        additionalName = "%",
        description = "",
    ),
    KillBreakPoint(
        shortName = "BP",
        additionalName = "Kill%",
        description = "",
    ),
    EfficiencyBreakPoint(
        shortName = "BP",
        additionalName = "Eff%",
        description = "",
    ),
    ErrorBreakPoint(
        shortName = "BP",
        additionalName = "Err%",
        description = "",
    ),
    KillSideOut(
        shortName = "SOut",
        additionalName = "Kill%",
        description = "",
    ),
    EfficiencySideOut(
        shortName = "SOut",
        additionalName = "Eff%",
        description = "",
    ),
    ErrorSideOut(
        shortName = "SOut",
        additionalName = "Err%",
        description = "",
    );

    override val id: String
        get() = this.name
}
