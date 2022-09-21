package com.kamilh.volleyballstats.storage.stats

import me.tatarka.inject.annotations.Provides

interface StatsModule {

    val SqlAttackStats.bind: AttackStats
        @Provides get() = this
}
