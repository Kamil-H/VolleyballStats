package com.kamilh.volleyballstats.storage.stats

import me.tatarka.inject.annotations.Provides

interface StatsModule {

    val SqlAttackStats.bind: AttackStatsStorage
        @Provides get() = this

    val SqlBlockStatsStorage.bind: BlockStatsStorage
        @Provides get() = this

    val SqlDigStatsStorage.bind: DigStatsStorage
        @Provides get() = this

    val SqlReceiveStatsStorage.bind: ReceiveStatsStorage
        @Provides get() = this

    val SqlServeStatsStorage.bind: ServeStatsStorage
        @Provides get() = this

    val SqlSetStatsStorage.bind: SetStatsStorage
        @Provides get() = this
}
