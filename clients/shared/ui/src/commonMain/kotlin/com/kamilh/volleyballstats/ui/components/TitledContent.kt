package com.kamilh.volleyballstats.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kamilh.volleyballstats.ui.extensions.conditional
import com.kamilh.volleyballstats.ui.theme.Dimens

@Composable
fun TitledContent(
    title: String?,
    contentMargin: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val margin = Dimens.MarginMedium
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(margin))
        if (title != null) {
            Text(
                modifier = Modifier.padding(start = margin, end = margin),
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(Dimens.MarginSmall))
        }
        Box(modifier = Modifier.conditional(contentMargin) { padding(start = margin, end = margin) }) {
            content()
        }
        Spacer(modifier = Modifier.height(margin))
    }
}
