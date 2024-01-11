package com.kamilh.volleyballstats.clients.jvmdesktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.bumble.appyx.navigation.integration.DesktopNodeHost
import com.kamilh.volleyballstats.clients.jvmdesktop.di.AppModule
import com.kamilh.volleyballstats.presentation.features.main.MainPresenter
import com.kamilh.volleyballstats.presentation.navigation.Screen
import com.kamilh.volleyballstats.ui.components.App
import com.kamilh.volleyballstats.ui.extensions.rememberPresenter
import com.kamilh.volleyballstats.ui.navigation.tab.TabContainer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow

fun main() = application {
    val events: Channel<Events> = Channel()
    val windowState = rememberWindowState(size = DpSize(480.dp, 658.dp))
    val appModule = remember { AppModule.instance }
    LaunchedEffect(appModule) {
        appModule.appInitializer.initialize()
    }
    Window(
        title = "Volleyball stats",
        state = windowState,
        onCloseRequest = ::exitApplication,
        onKeyEvent = { onKeyEvent(it, events) },
    ) {
        val scope = rememberCoroutineScope()
        val mainPresenter: MainPresenter = appModule.presenterMap.rememberPresenter(screen = Screen.Main)
        App(mainPresenter = mainPresenter) {
            DesktopNodeHost(
                windowState = windowState,
                onBackPressedEvents = events.receiveAsFlow().filterIsInstance<Events.OnBackPressed>().map { },
            ) { buildContext ->
                TabContainer(
                    coroutineScope = scope,
                    buildContext = buildContext,
                    presenterMap = appModule.presenterMap,
                    navigationEventReceiver = appModule.navigationEventReceiver,
                    onTabSelected = mainPresenter::onTabShown,
                )
            }
        }
    }
}

private sealed class Events {
    data object OnBackPressed : Events()
}

private fun onKeyEvent(keyEvent: KeyEvent, events: Channel<Events>): Boolean =
    if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Backspace) {
        events.trySend(Events.OnBackPressed)
        true
    } else false
