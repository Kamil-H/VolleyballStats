package com.kamilh.volleyballstats.clients.data

import com.kamilh.volleyballstats.api.MappersModule
import com.kamilh.volleyballstats.network.repository.PolishLeagueRepository
import com.kamilh.volleyballstats.storage.StorageModule
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface DataModule : MappersModule, StorageModule {

    val HttpStatsRepository.bindStatsRepository: StatsRepository
        @Provides get() = this

    val HttpStatsRepository.bindPolishLeagueRepository: PolishLeagueRepository
        @Provides get() = this
}
