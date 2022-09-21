package com.kamilh.volleyballstats.clients.data

import com.kamilh.volleyballstats.api.MappersModule
import com.kamilh.volleyballstats.network.repository.PolishLeagueRepository
import com.kamilh.volleyballstats.storage.StorageModule
import com.kamilh.volleyballstats.storage.stats.StatsModule
import me.tatarka.inject.annotations.Provides

interface DataModule : MappersModule, StorageModule, StatsModule {

    val HttpStatsRepository.bindStatsRepository: StatsRepository
        @Provides get() = this

    val HttpStatsRepository.bindPolishLeagueRepository: PolishLeagueRepository
        @Provides get() = this
}
