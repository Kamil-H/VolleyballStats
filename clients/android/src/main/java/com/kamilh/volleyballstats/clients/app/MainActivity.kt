package com.kamilh.volleyballstats.clients.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.domain.models.stats.StatsSkill
import com.kamilh.volleyballstats.presentation.navigation.NavigationEvent
import com.kamilh.volleyballstats.ui.extensions.FlowCollector
import com.kamilh.volleyballstats.ui.theme.VolleyballStatsTheme

class MainActivity : ComponentActivity() {

    private val appModule by lazy { AppModule.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VolleyballStatsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var statsSkill: StatsSkill? by remember { mutableStateOf(null) }

                    FlowCollector(flow = appModule.navigationEventReceiver.receive()) {
                        statsSkill = when (it) {
                            NavigationEvent.Close -> null
                            is NavigationEvent.PlayerFiltersRequested -> it.skill
                        }
                    }

                    if (statsSkill == null) {
                        PlayerStatsScreen(
                            modifier = Modifier.fillMaxSize(),
                            playerStatsPresenter = appModule.playerStatsPresenter,
                        )
                    } else {
                        PlayerFiltersScreen(
                            modifier = Modifier.fillMaxSize(),
                            playerFiltersPresenter = appModule.playerFiltersPresenterFactory.create(statsSkill!!),
                        )
                    }
                }
            }
        }
    }
}
