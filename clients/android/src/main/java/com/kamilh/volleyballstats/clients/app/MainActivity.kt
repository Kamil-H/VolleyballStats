package com.kamilh.volleyballstats.clients.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.kamilh.volleyballstats.clients.app.di.AppModule
import com.kamilh.volleyballstats.clients.app.ui.components.Table
import com.kamilh.volleyballstats.clients.app.ui.theme.EmptyComposeTheme
import com.kamilh.volleyballstats.presentation.features.players.PlayerStatsPresenter

class MainActivity : ComponentActivity() {

    private val appModule by lazy { AppModule.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmptyComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Screen(appModule.playerStatsPresenter)
                }
            }
        }
    }
}

@Composable
private fun Screen(playerStatsPresenter: PlayerStatsPresenter) {
    val state = playerStatsPresenter.state.collectAsState()
    Table(modifier = Modifier.fillMaxSize(), tableContent = state.value.tableContent)
}
