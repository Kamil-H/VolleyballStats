package com.kamilh.volleyballstats.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.kamilh.volleyballstats.domain.models.Url

@Composable
actual fun NetworkImage(
    url: Url,
    modifier: Modifier,
    contentDescription: String?,
) {
    AsyncImage(
        modifier = modifier,
        model = url.value,
        contentDescription = contentDescription,
    )
}
