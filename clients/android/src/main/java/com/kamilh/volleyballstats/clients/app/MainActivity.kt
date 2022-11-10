package com.kamilh.volleyballstats.clients.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.presentation.features.main.MainPresenter
import com.kamilh.volleyballstats.presentation.navigation.NavigationEvent
import com.kamilh.volleyballstats.ui.components.App
import com.kamilh.volleyballstats.ui.extensions.rememberPresenter

class MainActivity : ComponentActivity() {

    private val appModule by lazy { AppModule.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainPresenter: MainPresenter = appModule.rememberPresenter()

            App(mainPresenter = mainPresenter) {
                var statsSkill: StatsSkill? by remember { mutableStateOf(null) }
                val event by appModule.navigationEventReceiver.receive().collectAsState(
                    NavigationEvent.HomeTabRequested
                )

                when (val handledEvent = event) {
                    NavigationEvent.Close -> {
                        statsSkill = null
                        PlayersScreen(statsSkill = null)
                    }
                    NavigationEvent.HomeTabRequested -> EmptyScreen(title = "Home")
                    is NavigationEvent.PlayerFiltersRequested -> {
                        statsSkill = handledEvent.skill
                        PlayersScreen(statsSkill = statsSkill)
                    }
                    NavigationEvent.PlayersTabRequested -> PlayersScreen(statsSkill = statsSkill)
                    NavigationEvent.TeamsTabRequested -> EmptyScreen(title = "Teams")
                }
            }
        }
    }

    @Composable
    private fun PlayersScreen(statsSkill: StatsSkill?) {
        if (statsSkill == null) {
            PlayerStatsScreen(
                modifier = Modifier.fillMaxSize(),
                playerStatsPresenter = appModule.rememberPresenter(),
            )
        } else {
            PlayerFiltersScreen(
                modifier = Modifier.fillMaxSize(),
                playerFiltersPresenter = appModule.rememberPresenter(extras = statsSkill),
            )
        }
    }

    @Composable
    private fun EmptyScreen(modifier: Modifier = Modifier, title: String) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
