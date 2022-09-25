package com.kamilh.volleyballstats.presentation.features.players

import com.kamilh.volleyballstats.presentation.features.TableContent

data class PlayerStatsState(
    val tableContent: TableContent = TableContent(),
)

enum class AttackProperty(
    val shortName: String,
    val additionalName: String? = null,
    val description: String,
) {
    Index(
        shortName = "In.",
        description = "",
    ),
    Name(
        shortName = "Name",
        description = "",
    ),
    TeamName(
        shortName = "Team",
        description = "",
    ),
    Specialization(
        shortName = "Pos",
        description = "",
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
    ),
}

data class AttackConfiguration(
    val properties: List<AttackProperty> = AttackProperty.values().toList(),
)
