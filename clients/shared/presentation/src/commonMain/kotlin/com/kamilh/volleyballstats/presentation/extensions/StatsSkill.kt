package com.kamilh.volleyballstats.presentation.extensions

import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.presentation.features.Property
import com.kamilh.volleyballstats.presentation.features.players.properties.*

val StatsSkill.allProperties: List<Property<String>>
    get() = when (this) {
        StatsSkill.Attack -> AttackProperty.values()
        StatsSkill.Block -> BlockProperty.values()
        StatsSkill.Dig -> DigProperty.values()
        StatsSkill.Set -> SetProperty.values()
        StatsSkill.Receive -> ReceiveProperty.values()
        StatsSkill.Serve -> ServeProperty.values()
    }.toList()
