package com.kamilh.volleyballstats.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kamilh.volleyballstats.presentation.features.common.CellSize
import com.kamilh.volleyballstats.presentation.features.common.DataRow
import com.kamilh.volleyballstats.presentation.features.common.HeaderRow
import com.kamilh.volleyballstats.presentation.features.common.TableContent
import com.kamilh.volleyballstats.ui.extensions.ifNotNull
import com.kamilh.volleyballstats.ui.extensions.toDp
import com.kamilh.volleyballstats.ui.theme.Dimens

@Composable
fun Table(
    tableContent: TableContent,
    modifier: Modifier = Modifier,
    verticalLazyListState: LazyListState = rememberLazyListState(),
) {
    Table(
        modifier = modifier,
        verticalLazyListState = verticalLazyListState,
        rowModifier = {
            if (it.rem(2) == 0) {
                Modifier.background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f))
            } else {
                Modifier
            }
        },
        stickyHeaderModifier = Modifier.background(
            color = MaterialTheme.colorScheme.primary,
            shape = headerBackground(CornerSize(Dimens.CornerMedium)),
        ),
        stickyHeader = tableContent.header?.let { stickyRow ->
            { HeaderRow(row = stickyRow) }
        },
        columnCount = tableContent.columnCount,
        rowCount = tableContent.rowCount,
    ) {
        val row = tableContent.rows[rowIndex]
        TableRow(row = row)
    }
}

private fun headerBackground(size: CornerSize): CornerBasedShape =
    RoundedCornerShape(
        topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp), bottomStart = size, bottomEnd = size,
    )

@Composable
private fun TableRowScope.TableRow(row: DataRow, modifier: Modifier = Modifier) {
    val cell = row.cells[columnIndex]
    val horizontalSpacing = cell.size.horizontalSpacing

    TableRow(modifier = modifier, horizontalSpacing = horizontalSpacing) {
        Text(text = cell.content)
    }
}

@Composable
private fun TableRowScope.HeaderRow(row: HeaderRow, modifier: Modifier = Modifier) {
    val cell = row.cells[columnIndex]
    val horizontalSpacing = cell.size.horizontalSpacing

    TableRow(
        modifier = modifier.columnWidth().rowHeight(),
        horizontalSpacing = horizontalSpacing,
    ) {
        Column(
            modifier = Modifier.ifNotNull(cell.onClick) { onClick ->
                fillMaxSize().clickable { onClick() }
            }
        ) {
            HeaderCellText(text = cell.firstLine, selected = cell.selected)
            cell.secondLine?.let {
                HeaderCellText(text = it, selected = cell.selected)
            }
        }
    }
}

@Composable
private fun HeaderCellText(text: String, selected: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = text,
        maxLines = 1,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = if (selected) FontWeight.Bold else null,
    )
}

@Composable
private fun TableRow(horizontalSpacing: Dp, modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.height(Dimens.MarginSmall))
        Row {
            Spacer(modifier = Modifier.width(horizontalSpacing))
            content()
            Spacer(modifier = Modifier.width(horizontalSpacing))
        }
        Spacer(modifier = Modifier.height(Dimens.MarginSmall))
    }
}

private val CellSize.horizontalSpacing: Dp
    get() = when (this) {
        CellSize.Small -> Dimens.MarginSmall
        CellSize.Medium -> Dimens.MarginMedium
        CellSize.Large -> Dimens.MarginLarge
    }

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Table(
    columnCount: Int,
    rowCount: Int,
    modifier: Modifier = Modifier,
    stickyHeaderModifier: Modifier = Modifier,
    rowModifier: @Composable (rowIndex: Int) -> Modifier = { Modifier },
    verticalLazyListState: LazyListState = rememberLazyListState(),
    horizontalScrollState: ScrollState = rememberScrollState(),
    stickyHeader: (@Composable TableRowScope.() -> Unit)? = null,
    beforeRow: (@Composable (rowIndex: Int) -> Unit)? = null,
    afterRow: (@Composable (rowIndex: Int) -> Unit)? = null,
    cellContent: @Composable TableRowScope.() -> Unit
) {
    val columnWidths = remember { mutableStateMapOf<Int, Int>() }

    Box(modifier = modifier.horizontalScroll(horizontalScrollState)) {
        LazyColumn(state = verticalLazyListState) {
            stickyHeader?.let {
                stickyHeader {
                    TableRow(
                        rowModifier = { stickyHeaderModifier },
                        rowIndex = -1,
                        columnCount = columnCount,
                        columnWidths = columnWidths,
                    ) {
                        stickyHeader(this)
                    }
                }
            }
            items(rowCount) { rowIndex ->
                Column {
                    beforeRow?.invoke(rowIndex)
                    TableRow(
                        rowModifier = rowModifier,
                        rowIndex = rowIndex,
                        columnCount = columnCount,
                        columnWidths = columnWidths,
                        cellContent = cellContent,
                    )
                    afterRow?.invoke(rowIndex)
                }
            }
        }
    }
}

@Suppress("MutableParams")
@Composable
private fun TableRow(
    rowIndex: Int,
    columnCount: Int,
    columnWidths: MutableMap<Int, Int>,
    rowModifier: @Composable (rowIndex: Int) -> Modifier = { Modifier },
    cellContent: @Composable TableRowScope.() -> Unit,
) {
    var currentMaxHeight by rememberSaveable { mutableStateOf(0) }
    Row(modifier = rowModifier(rowIndex)) {
        (0 until columnCount).forEach { columnIndex ->
            Box(modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                val existingWidth = columnWidths[columnIndex] ?: 0
                val maxWidth = maxOf(existingWidth, placeable.width)

                if (maxWidth > existingWidth) {
                    columnWidths[columnIndex] = maxWidth
                }

                val maxHeight = maxOf(currentMaxHeight, placeable.height)
                if (maxHeight > currentMaxHeight) {
                    currentMaxHeight = maxHeight
                }

                layout(width = maxWidth, height = placeable.height) {
                    placeable.placeRelative(0, 0)
                }
            }) {
                cellContent(
                    TableRowScope(
                        columnIndex = columnIndex,
                        rowIndex = rowIndex,
                        width = (columnWidths[columnIndex] ?: 0).toDp(),
                        height = currentMaxHeight.toDp(),
                        boxScope = this,
                    )
                )
            }
        }
    }
}

private class TableRowScope(
    val columnIndex: Int,
    val rowIndex: Int,
    private val width: Dp,
    private val height: Dp,
    private val boxScope: BoxScope,
) : BoxScope by boxScope {

    @Stable
    fun Modifier.columnWidth(): Modifier = this.then(
        width(width)
    )

    @Stable
    fun Modifier.rowHeight(): Modifier = this.then(
        defaultMinSize(minHeight = height)
    )
}
