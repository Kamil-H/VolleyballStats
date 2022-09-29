package com.kamilh.volleyballstats.clients.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.presentation.features.players.PlayerStatsPresenter
import com.kamilh.volleyballstats.ui.components.SelectOption
import com.kamilh.volleyballstats.ui.components.Table
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
                    Screen(
                        modifier = Modifier.fillMaxSize(),
                        playerStatsPresenter = appModule.playerStatsPresenter,
                    )
                }
            }
        }
    }
}

@Composable
private fun Screen(
    modifier: Modifier = Modifier,
    playerStatsPresenter: PlayerStatsPresenter
) {
    val state = playerStatsPresenter.state.collectAsState()
    Column(modifier = modifier) {
        Table(
            modifier = Modifier.weight(1f),
            tableContent = state.value.tableContent,
        )
        SelectOption(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.surface),
            singleLine = true,
            selectOptionState = state.value.selectSkillState,
        )
    }
}
