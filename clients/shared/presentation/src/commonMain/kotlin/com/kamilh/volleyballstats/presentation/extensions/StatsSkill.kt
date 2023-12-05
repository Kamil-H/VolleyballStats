package com.kamilh.volleyballstats.presentation.extensions

import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.domain.models.stats.StatsType
import com.kamilh.volleyballstats.presentation.features.common.Property
import com.kamilh.volleyballstats.presentation.features.stats.properties.AttackProperty
import com.kamilh.volleyballstats.presentation.features.stats.properties.BlockProperty
import com.kamilh.volleyballstats.presentation.features.stats.properties.DigProperty
import com.kamilh.volleyballstats.presentation.features.stats.properties.ReceiveProperty
import com.kamilh.volleyballstats.presentation.features.stats.properties.ServeProperty
import com.kamilh.volleyballstats.presentation.features.stats.properties.SetProperty

fun StatsSkill.allProperties(type: StatsType): List<Property<String>> = when (this) {
    StatsSkill.Attack -> AttackProperty.values()
    StatsSkill.Block -> BlockProperty.values()
    StatsSkill.Dig -> DigProperty.values()
    StatsSkill.Set -> SetProperty.values()
    StatsSkill.Receive -> ReceiveProperty.values()
    StatsSkill.Serve -> ServeProperty.values()
}.toList() - propertiesToRemove(skill = this, type)

@Suppress("CyclomaticComplexMethod")
private fun propertiesToRemove(skill: StatsSkill, type: StatsType): Set<Property<String>> =
    when (skill) {
        StatsSkill.Attack -> when (type) {
            StatsType.Player -> setOf(AttackProperty.FullTeamName)
            StatsType.Team -> setOf(AttackProperty.Name, AttackProperty.TeamName, AttackProperty.Specialization)
        }
        StatsSkill.Block -> when (type) {
            StatsType.Player -> setOf(BlockProperty.FullTeamName)
            StatsType.Team -> setOf(BlockProperty.Name, BlockProperty.TeamName, BlockProperty.Specialization)
        }
        StatsSkill.Dig -> when (type) {
            StatsType.Player -> setOf(DigProperty.FullTeamName)
            StatsType.Team -> setOf(DigProperty.Name, DigProperty.TeamName, DigProperty.Specialization)
        }
        StatsSkill.Set -> when (type) {
            StatsType.Player -> setOf(SetProperty.FullTeamName)
            StatsType.Team -> setOf(SetProperty.Name, SetProperty.TeamName, SetProperty.Specialization)
        }
        StatsSkill.Receive -> when (type) {
            StatsType.Player -> setOf(ReceiveProperty.FullTeamName)
            StatsType.Team -> setOf(ReceiveProperty.Name, ReceiveProperty.TeamName, ReceiveProperty.Specialization)
        }
        StatsSkill.Serve -> when (type) {
            StatsType.Player -> setOf(ServeProperty.FullTeamName)
            StatsType.Team -> setOf(ServeProperty.Name, ServeProperty.TeamName, ServeProperty.Specialization)
        }
    }
