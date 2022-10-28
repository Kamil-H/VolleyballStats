package com.kamilh.volleyballstats.presentation.navigation

import com.kamilh.volleyballstats.domain.di.Singleton
import com.kamilh.volleyballstats.domain.utils.AppDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

interface NavigationEventSender {

    fun send(navigationEvent: NavigationEvent)
}

interface NavigationEventReceiver {

    fun receive(): Flow<NavigationEvent>
}

@Inject
@Singleton
class NavigationEventManager(
    private val coroutineScope: CoroutineScope,
    private val appDispatchers: AppDispatchers,
) : NavigationEventSender, NavigationEventReceiver {

    private val eventChannel = Channel<NavigationEvent>(capacity = Channel.UNLIMITED)

    override fun send(navigationEvent: NavigationEvent) {
        coroutineScope.launch(context = appDispatchers.main) {
            eventChannel.send(navigationEvent)
        }
    }

    override fun receive(): Flow<NavigationEvent> =
        eventChannel.receiveAsFlow()
}
