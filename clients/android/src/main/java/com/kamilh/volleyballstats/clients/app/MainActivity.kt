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
                    var showStatsScreen by remember { mutableStateOf(true) }
                    if (showStatsScreen) {
                        PlayerStatsScreen(
                            modifier = Modifier.fillMaxSize(),
                            playerStatsPresenter = appModule.playerStatsPresenter,
                        ) {
                            showStatsScreen = false
                        }
                    } else {
                        PlayerFiltersScreen(
                            modifier = Modifier.fillMaxSize(),
                            playerFiltersPresenter = appModule.playerFiltersPresenter,
                        ) {
                            showStatsScreen = true
                        }
                    }
                }
            }
        }
    }
}
