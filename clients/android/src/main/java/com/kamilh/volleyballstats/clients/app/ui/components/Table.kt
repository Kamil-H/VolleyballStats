package com.kamilh.volleyballstats.clients.app.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kamilh.volleyballstats.presentation.features.TableCell
import com.kamilh.volleyballstats.presentation.features.TableContent
import com.kamilh.volleyballstats.presentation.features.TableRow

@Composable
fun Table(modifier: Modifier = Modifier, tableContent: TableContent) {
    Table(
        modifier = modifier,
        rowModifier = {
            if (it.rem(2) == 0) {
                Modifier.background(color = MaterialTheme.colors.onBackground.copy(alpha = 0.04f))
            } else {
                Modifier
            }
        },
        stickyHeaderModifier = Modifier.background(color = MaterialTheme.colors.background),
        stickyHeader = tableContent.header?.let { stickyRow ->
            { columnIndex -> TableRow(row = stickyRow, columnIndex = columnIndex) }
        },
        columnCount = tableContent.rows.firstOrNull()?.cells?.size ?: 0,
        rowCount = tableContent.rows.size
    ) { columnIndex, rowIndex ->
        val row = tableContent.rows[rowIndex]
        TableRow(row = row, columnIndex = columnIndex)
    }
}

@Composable
private fun TableRow(modifier: Modifier = Modifier, row: TableRow, columnIndex: Int) {
    val cell = row.cells[columnIndex]
    val horizontalSpacing = cell.size.horizontalSpacing

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Spacer(modifier = Modifier.width(horizontalSpacing))
            Text(text = cell.content)
            Spacer(modifier = Modifier.width(horizontalSpacing))
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private val TableCell.Size.horizontalSpacing: Dp
    get() = when (this) {
        TableCell.Size.Small -> 8.dp
        TableCell.Size.Medium -> 12.dp
        TableCell.Size.Big -> 16.dp
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
    stickyHeader: (@Composable (columnIndex: Int) -> Unit)? = null,
    beforeRow: (@Composable (rowIndex: Int) -> Unit)? = null,
    afterRow: (@Composable (rowIndex: Int) -> Unit)? = null,
    cellContent: @Composable (columnIndex: Int, rowIndex: Int) -> Unit
) {
    val columnWidths = remember { mutableStateMapOf<Int, Int>() }

    Box(modifier = modifier.then(Modifier.horizontalScroll(horizontalScrollState))) {
        LazyColumn(state = verticalLazyListState) {
            stickyHeader?.let {
                stickyHeader {
                    TableRow(
                        rowModifier = { stickyHeaderModifier },
                        rowIndex = -1,
                        columnCount = columnCount,
                        columnWidths = columnWidths,
                    ) { columnIndex, _ ->
                        stickyHeader(columnIndex)
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
    cellContent: @Composable (columnIndex: Int, rowIndex: Int) -> Unit,
) {
    Row(modifier = rowModifier(rowIndex)) {
        (0 until columnCount).forEach { columnIndex ->
            Box(modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                val existingWidth = columnWidths[columnIndex] ?: 0
                val maxWidth = maxOf(existingWidth, placeable.width)

                if (maxWidth > existingWidth) {
                    columnWidths[columnIndex] = maxWidth
                }

                layout(width = maxWidth, height = placeable.height) {
                    placeable.placeRelative(0, 0)
                }
            }) {
                cellContent(columnIndex, rowIndex)
            }
        }
    }
}
