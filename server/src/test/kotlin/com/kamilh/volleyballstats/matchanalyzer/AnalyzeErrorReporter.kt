package com.kamilh.volleyballstats.matchanalyzer

fun analyzeErrorReporterOf(
    errors: MutableList<AnalyzeError> = mutableListOf()
): AnalyzeErrorReporter = object : AnalyzeErrorReporter {

    override fun report(analyzeError: AnalyzeError) {
        errors.add(analyzeError)
    }
}