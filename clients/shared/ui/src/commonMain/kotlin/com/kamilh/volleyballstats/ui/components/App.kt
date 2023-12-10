package com.kamilh.volleyballstats.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kamilh.volleyballstats.presentation.features.ColorAccent
import com.kamilh.volleyballstats.presentation.features.main.MainPresenter
import com.kamilh.volleyballstats.ui.extensions.toPainter
import com.kamilh.volleyballstats.ui.theme.VolleyballStatsTheme

@Composable
fun App(
    mainPresenter: MainPresenter,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    VolleyballStatsTheme(colorAccent = ColorAccent.Default) {
        val state by mainPresenter.state.collectAsState()
        Scaffold(
            modifier = modifier,
            bottomBar = {
                NavigationBar {
                    state.bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = item.selected,
                            onClick = { item.onClicked(item.id) },
                            icon = {
                                Icon(painter = item.icon.toPainter(), contentDescription = null)
                            },
                            label = { Text(text = item.label) },
                        )
                    }
                }
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding()),
                color = MaterialTheme.colorScheme.background
            ) {
                content(PaddingValues())
            }
        }
    }
}
