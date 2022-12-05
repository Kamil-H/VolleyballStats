package com.kamilh.volleyballstats.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.kamilh.volleyballstats.presentation.features.common.TextPair

@Composable
fun TextPairView(
    textPair: TextPair,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
) {
    Row(modifier = modifier) {
        Text(
            text = textPair.first,
            fontWeight = FontWeight.Bold,
            style = style,
        )
        Text(
            text = textPair.spacer,
            fontWeight = FontWeight.Bold,
            style = style,
        )
        Text(text = textPair.second, style = style)
    }
}
