package com.kamilh.volleyballstats.matchanalyzer

import com.kamilh.volleyballstats.models.Event

fun eventsPreparerOf(
    prepare: List<Event> = emptyList()
): EventsPreparer = object : EventsPreparer {

    override fun prepare(events: List<Event>): List<Event> = prepare.ifEmpty { events }
}