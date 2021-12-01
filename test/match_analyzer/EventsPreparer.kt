package com.kamilh.match_analyzer

import com.kamilh.models.Event

fun eventsPreparerOf(
    prepare: List<Event> = emptyList()
): EventsPreparer = object : EventsPreparer {

    override fun prepare(events: List<Event>): List<Event> = prepare.ifEmpty { events }
}