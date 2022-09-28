package com.kamilh.volleyballstats.clients.app.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kamilh.volleyballstats.presentation.features.CellSize
import com.kamilh.volleyballstats.presentation.features.DataRow
import com.kamilh.volleyballstats.presentation.features.HeaderRow
import com.kamilh.volleyballstats.presentation.features.TableContent

@Composable
fun Table(modifier: Modifier = Modifier, tableContent: TableContent) {
    Table(
        modifier = modifier,
        rowModifier = {
            if (it.rem(2) == 0) {
                Modifier.background(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f))
            } else {
                Modifier
            }
        },
        stickyHeaderModifier = Modifier.background(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(8.dp),
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

@Composable
private fun TableRowScope.TableRow(modifier: Modifier = Modifier, row: DataRow) {
    val cell = row.cells[columnIndex]
    val horizontalSpacing = cell.size.horizontalSpacing

    TableRow(modifier = modifier, horizontalSpacing = horizontalSpacing) {
        Text(text = cell.content)
    }
}

@Composable
private fun TableRowScope.HeaderRow(modifier: Modifier = Modifier, row: HeaderRow) {
    val cell = row.cells[columnIndex]
    val horizontalSpacing = cell.size.horizontalSpacing

    TableRow(
        modifier = modifier.columnWidth().rowHeight(),
        horizontalSpacing = horizontalSpacing,
    ) {
        Column {
            HeaderCellText(text = cell.firstLine)
            cell.secondLine?.let {
                HeaderCellText(text = it)
            }
        }
    }
}

@Composable
private fun HeaderCellText(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text,
        maxLines = 1,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onPrimary,
    )
}

@Composable
private fun TableRow(modifier: Modifier = Modifier, horizontalSpacing: Dp, content: @Composable RowScope.() -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Spacer(modifier = Modifier.width(horizontalSpacing))
            content()
            Spacer(modifier = Modifier.width(horizontalSpacing))
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private val CellSize.horizontalSpacing: Dp
    get() = when (this) {
        CellSize.Small -> 8.dp
        CellSize.Medium -> 12.dp
        CellSize.Big -> 16.dp
    }

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Table(
    modifier: Modifier = Modifier,
    rowModifier: @Composable (rowIndex: Int) -> Modifier = { Modifier },
    stickyHeaderModifier: Modifier = Modifier,
    verticalLazyListState: LazyListState = rememberLazyListState(),
    horizontalScrollState: ScrollState = rememberScrollState(),
    columnCount: Int,
    rowCount: Int,
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

@Composable
private fun TableRow(
    rowModifier: @Composable (rowIndex: Int) -> Modifier = { Modifier },
    rowIndex: Int,
    columnCount: Int,
    columnWidths: MutableMap<Int, Int>,
    cellContent: @Composable TableRowScope.() -> Unit,
) {
    var currentMaxHeight by remember { mutableStateOf(0) }
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
                if (maxWidth > currentMaxHeight) {
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

@Composable
private fun Int.toDp(): Dp =
    with(LocalDensity.current) {
        this@toDp.toDp()
    }
