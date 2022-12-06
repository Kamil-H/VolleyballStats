package com.kamilh.volleyballstats.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kamilh.volleyballstats.presentation.features.main.MainPresenter
import com.kamilh.volleyballstats.ui.extensions.toPainter
import com.kamilh.volleyballstats.ui.theme.VolleyballStatsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    mainPresenter: MainPresenter,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    VolleyballStatsTheme {
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
