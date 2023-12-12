package com.kamilh.volleyballstats.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.fetch.NetworkFetcher
import coil3.request.ImageRequest
import com.kamilh.volleyballstats.domain.models.Url

@OptIn(ExperimentalCoilApi::class)
@Composable
fun NetworkImage(
    url: Url,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val fetcherFactory = remember { NetworkFetcher.Factory() }
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(url.value)
            .fetcherFactory(fetcherFactory)
            .build(),
        contentDescription = contentDescription,
    )
}
