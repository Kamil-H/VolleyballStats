package com.kamilh.match_analyzer

import com.kamilh.match_analyzer.strategies.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

private const val MODULE_NAME = "DI_MATCH_ANALYZER_MODULE"
val matchAnalyzerModule = DI.Module(name = MODULE_NAME) {

    bind<List<PlayActionStrategy<*>>>() with provider {
        listOf(AttackStrategy(), BlockStrategy(), DigStrategy(), FreeballStrategy(), ReceiveStrategy(), ServeStrategy(), SetStrategy())
    }

    bind<MatchReportAnalyzer>() with provider {
        MatchReportAnalyzer(instance(), instance(), instance(), instance())
    }

    bind<EventsPreparer>() with provider { EventsPreparerImpl() }

    bind<AnalyzeErrorReporter>() with provider { PrintingAnalyzeErrorReporter() }
}