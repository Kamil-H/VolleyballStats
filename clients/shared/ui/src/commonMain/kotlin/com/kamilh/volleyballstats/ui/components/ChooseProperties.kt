package com.kamilh.volleyballstats.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.kamilh.volleyballstats.presentation.features.common.CheckableProperty
import com.kamilh.volleyballstats.presentation.features.common.ChoosePropertiesState
import com.kamilh.volleyballstats.presentation.features.common.title
import com.kamilh.volleyballstats.ui.extensions.conditional
import com.kamilh.volleyballstats.ui.theme.Dimens

@Composable
fun <T: Any> ChooseProperties(
    choosePropertiesState: ChoosePropertiesState<T>,
    modifier: Modifier = Modifier,
) {
    ChooseProperties(
        modifier = modifier,
        title = choosePropertiesState.title,
        checkableProperties = choosePropertiesState.checkableProperties,
        visible = choosePropertiesState.visible,
        onChecked = choosePropertiesState.onChecked,
    )
}

@Suppress("UnstableCollections")
@Composable
private fun <T: Any> ChooseProperties(
    title: String?,
    checkableProperties: List<CheckableProperty<T>>,
    visible: Boolean,
    modifier: Modifier = Modifier,
    onChecked: (T) -> Unit,
) {

    if (visible) {
        TitledContent(modifier = modifier, title = title, contentMargin = true) {
            Column(
                verticalArrangement = Arrangement.spacedBy(space = Dimens.MarginSmall),
            ) {
                checkableProperties.forEachIndexed { index, item ->
                    CheckablePropertyItem(
                        modifier = Modifier,
                        property = item,
                    ) { onChecked(it) }
                    if (index < checkableProperties.size - 1) {
                        Divider(modifier = Modifier.height(1.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("MagicNumber")
@Composable
private fun <T: Any> CheckablePropertyItem(
    property: CheckableProperty<T>,
    modifier: Modifier = Modifier,
    onChecked: (T) -> Unit,
) {
    Row(
        modifier = modifier.conditional(!property.mandatory) {
            clickable { onChecked(property.id) }
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(space = Dimens.MarginExtraSmall),
        ) {
            val alphaModifier = Modifier.alpha(if (property.checked) 1.0f else 0.3f)
            Text(
                text = property.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = alphaModifier,
            )
            Text(
                text = property.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = alphaModifier,
            )
        }
        Checkbox(
            checked = property.checked,
            onCheckedChange = { onChecked(property.id) },
            enabled = !property.mandatory,
        )
    }
}
