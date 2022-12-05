package com.kamilh.volleyballstats.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kamilh.volleyballstats.domain.models.Url

@Composable
expect fun NetworkImage(
    url: Url,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)
