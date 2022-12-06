package com.kamilh.volleyballstats.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kamilh.volleyballstats.presentation.features.common.GroupedMatchItem
import com.kamilh.volleyballstats.presentation.features.common.MatchItem
import com.kamilh.volleyballstats.ui.extensions.snapToIndex
import com.kamilh.volleyballstats.ui.theme.Dimens

@OptIn(ExperimentalFoundationApi::class)
@Suppress("MutableParams", "UnstableCollections")
@Composable
fun MatchList(
    groupedMatchItems: List<GroupedMatchItem>,
    modifier: Modifier = Modifier,
    itemToSnapTo: Int = 0,
    state: LazyListState = rememberLazyListState(),
) {

    state.snapToIndex(index = itemToSnapTo)

    LazyColumn(
        modifier = modifier.padding(horizontal = Dimens.MarginMedium),
        contentPadding = PaddingValues(bottom = Dimens.MarginMedium),
        state = state,
    ) {
        groupedMatchItems.forEach { (title, matchItems) ->
            stickyHeader {
                Text(
                    text = title,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(vertical = Dimens.MarginSmall),
                )
            }
            itemsIndexed(
                items = matchItems,
                key = { _, item -> item.id }
            ) { index, item ->
                MatchItem(
                    modifier = Modifier,
                    matchItem = item
                )
                if (index < matchItems.size - 1) {
                    Spacer(modifier = Modifier.height(Dimens.MarginMedium))
                }
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun MatchItem(
    matchItem: MatchItem,
    modifier: Modifier = Modifier,
) {

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Column(
            modifier = Modifier.padding(all = Dimens.MarginMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(space = Dimens.MarginSmall)) {
                SideView(
                    modifier = Modifier
                        .weight(1f)
                        .align(alignment = Alignment.Top),
                    sideDetails = matchItem.left
                )
                Text(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .weight(0.6f),
                    text = matchItem.centerText,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
                SideView(
                    modifier = Modifier
                        .weight(1f)
                        .align(alignment = Alignment.Top),
                    sideDetails = matchItem.right
                )
            }
            matchItem.bottomText?.let {
                TextPairView(
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    textPair = it,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun SideView(
    sideDetails: MatchItem.SideDetails,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            space = Dimens.MarginSmall,
            alignment = Alignment.CenterVertically
        )
    ) {
        NetworkImage(
            url = sideDetails.imageUrl,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
        )
        Text(
            text = sideDetails.label,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
        )
    }
}
