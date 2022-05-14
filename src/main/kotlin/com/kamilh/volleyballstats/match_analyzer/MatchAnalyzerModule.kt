package com.kamilh.volleyballstats.match_analyzer

import com.kamilh.volleyballstats.match_analyzer.strategies.*
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface MatchAnalyzerModule {

    @Provides
    fun strategies(
        attackStrategy: AttackStrategy, blockStrategy: BlockStrategy, digStrategy: DigStrategy, freeballStrategy: FreeballStrategy,
        receiveStrategy: ReceiveStrategy, serveStrategy: ServeStrategy, setStrategy: SetStrategy,
    ): List<PlayActionStrategy<*>> =
        listOf(attackStrategy, blockStrategy, digStrategy, freeballStrategy, receiveStrategy, serveStrategy, setStrategy)

    val MatchReportAnalyzerInteractor.bind: MatchReportAnalyzer
        @Provides get() = this

    val EventsPreparerImpl.bind: EventsPreparer
        @Provides get() = this

    val PrintingAnalyzeErrorReporter.bind : AnalyzeErrorReporter
        @Provides get() = this
}