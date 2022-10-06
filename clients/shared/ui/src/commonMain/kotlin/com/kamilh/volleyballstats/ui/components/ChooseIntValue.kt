package com.kamilh.volleyballstats.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kamilh.volleyballstats.presentation.features.ChooseIntState

@Composable
fun ChooseIntValue(
    modifier: Modifier = Modifier,
    chooseIntState: ChooseIntState,
) {
    ChooseIntValue(
        modifier = modifier,
        title = chooseIntState.title,
        value = chooseIntState.value,
        maxValue = chooseIntState.maxValue,
        onValueSelected = chooseIntState.onValueSelected,
    )
}

@Composable
private fun ChooseIntValue(
    modifier: Modifier = Modifier,
    title: String,
    value: Int,
    maxValue: Int,
    onValueSelected: (Int) -> Unit,
) {

    TitledContent(modifier = modifier, title = title, contentMargin = true) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueSelected(it.toInt()) },
                valueRange = 0f.rangeTo(maxValue.toFloat()),
                steps = 100,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.defaultMinSize(minWidth = 50.dp)
            )
        }
    }
}
