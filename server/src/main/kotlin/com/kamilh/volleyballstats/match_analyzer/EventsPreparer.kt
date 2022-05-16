package com.kamilh.volleyballstats.match_analyzer

import com.kamilh.volleyballstats.models.Event
import me.tatarka.inject.annotations.Inject

interface EventsPreparer {

    fun prepare(events: List<Event>): List<Event>
}

@Inject
class EventsPreparerImpl : EventsPreparer {

    override fun prepare(events: List<Event>): List<Event> {
        val liberoIndexes = events
            .asSequence()
            .filterIsInstance<Event.VideoChallenge>()
            .filter { it.scoreChange == Event.VideoChallenge.ScoreChange.AssignToOther || it.scoreChange == Event.VideoChallenge.ScoreChange.RepeatLast }
            .map(events::indexOf)
            .flatMap {
                val videoChallengeIndex = it
                var rallyIndex: Int? = null
                for (i in it downTo 0) {
                    val event = events[i]
                    if (event is Event.Rally) {
                        rallyIndex = i
                        break
                    }
                }
                rallyIndex ?: error("No Rally before VideoChallenge")
                events.subList(rallyIndex, videoChallengeIndex).filterIsInstance<Event.Libero>()
            }
        return events.filter { !liberoIndexes.contains(it) }
    }
}