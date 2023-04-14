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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kamilh.volleyballstats.presentation.features.common.CellSize
import com.kamilh.volleyballstats.presentation.features.common.DataRow
import com.kamilh.volleyballstats.presentation.features.common.HeaderRow
import com.kamilh.volleyballstats.presentation.features.common.TableContent
import com.kamilh.volleyballstats.ui.extensions.conditional
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
            if (rowIndex.rem(2) == 0) {
                Modifier.background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f))
            } else {
                Modifier
            }
        },
        stickyRowItemsCount = tableContent.stickyRowItemsCount,
        stickyHeaderModifier = {
            Modifier.background(
                color = MaterialTheme.colorScheme.primary,
                shape = headerBackground(
                    size = CornerSize(Dimens.CornerMedium),
                    roundStart = !scrollable || tableContent.stickyRowItemsCount == 0,
                    roundEnd = scrollable,
                ),
            )
        },
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

private fun headerBackground(
    size: CornerSize,
    roundStart: Boolean = true,
    roundEnd: Boolean = true,
): CornerBasedShape = RoundedCornerShape(
    topStart = CornerSize(0.dp),
    topEnd = CornerSize(0.dp),
    bottomStart = if (roundStart) size else CornerSize(0.dp),
    bottomEnd = if (roundEnd) size else CornerSize(0.dp),
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
        modifier = modifier
            .columnWidth()
            .rowHeight()
            .ifNotNull(cell.onClick) { onClick ->
                fillMaxSize().clickable { onClick() }
            }
            .conditional(cell.selected) {
                background(color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            },
        horizontalSpacing = horizontalSpacing,
    ) {
        Column {
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
    Row(
        modifier.padding(vertical = Dimens.MarginSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(horizontalSpacing))
        content()
        Spacer(modifier = Modifier.width(horizontalSpacing))
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
    stickyRowItemsCount: Int,
    modifier: Modifier = Modifier,
    stickyHeaderModifier: @Composable RowModifierScope.() -> Modifier = { Modifier },
    rowModifier: @Composable RowModifierScope.() -> Modifier = { Modifier },
    verticalLazyListState: LazyListState = rememberLazyListState(),
    horizontalScrollState: ScrollState = rememberScrollState(),
    stickyHeader: (@Composable TableRowScope.() -> Unit)? = null,
    beforeRow: (@Composable (rowIndex: Int) -> Unit)? = null,
    afterRow: (@Composable (rowIndex: Int) -> Unit)? = null,
    cellContent: @Composable TableRowScope.() -> Unit
) {
    val columnWidths = remember { mutableStateMapOf<Int, Int>() }

    Box(modifier = modifier) {
        LazyColumn(state = verticalLazyListState) {
            stickyHeader?.let {
                stickyHeader {
                    TableRow(
                        rowModifier = { stickyHeaderModifier() },
                        rowIndex = -1,
                        stickyRowItemsCount = stickyRowItemsCount,
                        horizontalScrollState = horizontalScrollState,
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
                        stickyRowItemsCount = stickyRowItemsCount,
                        horizontalScrollState = horizontalScrollState,
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
    stickyRowItemsCount: Int,
    columnWidths: MutableMap<Int, Int>,
    horizontalScrollState: ScrollState,
    rowModifier: @Composable RowModifierScope.() -> Modifier = { Modifier },
    cellContent: @Composable TableRowScope.() -> Unit,
) {
    val currentMaxHeight = rememberSaveable { mutableStateOf(0) }
    val indexes = remember { RowIndexes(columnCount = columnCount, stickyRowItemsCount = stickyRowItemsCount) }
    Row {
        Box {
            TableRow(
                rowIndex = rowIndex,
                indexes = indexes.stickyIndexes,
                columnWidths = columnWidths,
                currentMaxHeight = currentMaxHeight,
                horizontalScrollState = null,
                rowModifier = rowModifier,
                cellContent = cellContent,
            )
            if (horizontalScrollState.value > 0) {
                VerticalDivider(
                    minHeight = currentMaxHeight.value.toDp(),
                    rowIndex = rowIndex,
                )
            }
        }
        TableRow(
            rowIndex = rowIndex,
            indexes = indexes.columnIndexes,
            columnWidths = columnWidths,
            currentMaxHeight = currentMaxHeight,
            horizontalScrollState = horizontalScrollState,
            rowModifier = rowModifier,
            cellContent = cellContent,
        )
    }
}

@Composable
private fun BoxScope.VerticalDivider(
    minHeight: Dp,
    rowIndex: Int,
    modifier: Modifier = Modifier,
) {
    VerticalDivider(
        modifier = modifier,
        minHeight = minHeight,
        color = (if (rowIndex == -1) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.onBackground
        }).copy(alpha = 0.2f)
    )
}

@Composable
private fun BoxScope.VerticalDivider(
    minHeight: Dp,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.defaultMinSize(minHeight = minHeight)
        .width(2.dp)
        .background(color = color)
        .align(Alignment.CenterEnd)
    )
}

@Composable
@Suppress("MutableParams")
private fun TableRow(
    rowIndex: Int,
    indexes: IntRange,
    columnWidths: MutableMap<Int, Int>,
    currentMaxHeight: MutableState<Int>,
    horizontalScrollState: ScrollState? = null,
    rowModifier: @Composable RowModifierScope.() -> Modifier = { Modifier },
    cellContent: @Composable TableRowScope.() -> Unit,
) {
    Row(modifier = Modifier.ifNotNull(horizontalScrollState, Modifier::horizontalScroll)
        .then(
            RowModifierScope(
                rowIndex = rowIndex,
                scrollable = horizontalScrollState != null,
            ).rowModifier()
        )
    ) {
        indexes.forEach { columnIndex ->
            Box(modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                val existingWidth = columnWidths[columnIndex] ?: 0
                val maxWidth = maxOf(existingWidth, placeable.width)

                if (maxWidth > existingWidth) {
                    columnWidths[columnIndex] = maxWidth
                }

                val maxHeight = maxOf(currentMaxHeight.value, placeable.height)
                if (maxHeight > currentMaxHeight.value) {
                    currentMaxHeight.value = maxHeight
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
                        height = currentMaxHeight.value.toDp(),
                        boxScope = this,
                    )
                )
            }
        }
    }
}

private class RowModifierScope(
    val rowIndex: Int,
    val scrollable: Boolean,
)

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

private class RowIndexes(columnCount: Int, stickyRowItemsCount: Int) {
    private val allIndexes: IntRange = 0 until columnCount

    val stickyIndexes: IntRange = if (stickyRowItemsCount <= columnCount) {
        when (stickyRowItemsCount) {
            0 -> IntRange.EMPTY
            columnCount -> allIndexes
            else -> 0 until stickyRowItemsCount
        }
    } else {
        error("stickyRowItemsCount ($stickyRowItemsCount) must be <= columnCount ($columnCount)")
    }

    val columnIndexes: IntRange = if (stickyRowItemsCount <= columnCount) {
        when (stickyRowItemsCount) {
            0 -> allIndexes
            columnCount -> IntRange.EMPTY
            else -> stickyRowItemsCount until columnCount
        }
    } else {
        error("stickyRowItemsCount ($stickyRowItemsCount) must be <= columnCount ($columnCount)")
    }
}
