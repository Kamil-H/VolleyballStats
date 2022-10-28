package com.kamilh.volleyballstats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.kamilh.volleyballstats.presentation.features.SelectOptionState
import com.kamilh.volleyballstats.ui.theme.Dimens

@Composable
fun <T : Any> SelectOption(
    selectOptionState: SelectOptionState<T>,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
) {
    if (selectOptionState.visible) {
        TitledContent(modifier = modifier, title = selectOptionState.title, contentMargin = !singleLine) {
            ChipLayout(singleLine = singleLine, selectOptionState = selectOptionState)
        }
    }
}

@Composable
private fun <T : Any> ChipLayout(
    singleLine: Boolean,
    selectOptionState: SelectOptionState<T>,
    modifier: Modifier = Modifier,
) {
    val spacing = Dimens.MarginSmall
    if (singleLine) {
        LazyChipRow(modifier = modifier, spacing = spacing, selectOptionState = selectOptionState)
    } else {
        ChipGroup(modifier = modifier, spacing = spacing, selectOptionState = selectOptionState)
    }
}

@Composable
private fun <T: Any> LazyChipRow(
    selectOptionState: SelectOptionState<T>,
    modifier: Modifier = Modifier,
    spacing: Dp = Dimens.MarginSmall,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = Dimens.MarginMedium, end = Dimens.MarginMedium)
    ) {
        itemsIndexed(
            items = selectOptionState.options,
            key = { _, item -> item.id }
        ) { index, item ->
            Chip(option = item) {
                selectOptionState.onSelected(item.id)
            }
            if (index < selectOptionState.options.size - 1) {
                Spacer(modifier = Modifier.width(spacing).background(color = Color.Magenta))
            }
        }
    }
}

@Composable
private fun <T: Any> ChipGroup(
    selectOptionState: SelectOptionState<T>,
    modifier: Modifier = Modifier,
    spacing: Dp = Dimens.MarginSmall,
) {
    ChipGroup(
        mainAxisSpacing = spacing,
        modifier = modifier,
    ) {
        selectOptionState.options.forEach { option ->
            Chip(option = option) {
                selectOptionState.onSelected(option.id)
            }
        }
    }
}

@Composable
private fun <T : Any> Chip(
    option: SelectOptionState.Option<T>,
    modifier: Modifier = Modifier,
    onClick: (T) -> Unit,
) {
    Chip(
        modifier = modifier,
        id = option.id,
        label = option.label,
        selected = option.selected,
    ) { onClick(it) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T : Any> Chip(
    id: T,
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (T) -> Unit,
) {
    ElevatedFilterChip(
        modifier = modifier,
        selected = selected,
        onClick = { onClick(id) },
        label = { Text(text = label) }
    )
}
