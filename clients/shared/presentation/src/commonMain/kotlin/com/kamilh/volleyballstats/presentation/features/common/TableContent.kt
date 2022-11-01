package com.kamilh.volleyballstats.presentation.features.common

typealias DataRow = TableRow<DataCell>
typealias HeaderRow = TableRow<HeaderCell>

data class TableContent(
    val rows: List<DataRow> = emptyList(),
    val header: HeaderRow? = null,
) {
    val rowCount: Int = rows.size

    val columnCount: Int = rows.firstOrNull()?.cells?.size ?: 0

    init {
        val rowsSizes = rows.map { it.cells.size }.toSet()
        if (rowsSizes.isNotEmpty() && header != null && header.cells.size != rowsSizes.first()) {
            error("rowSizes: $rowsSizes, header.cells.size: ${header.cells.size}")
        }
    }
}

data class TableRow<T : TableCell>(
    val cells: List<T> = emptyList(),
    val isSelected: Boolean = false,
)

sealed interface TableCell

data class DataCell(
    val content: String,
    val size: CellSize,
) : TableCell

data class HeaderCell(
    val firstLine: String,
    val secondLine: String? = null,
    val size: CellSize,
    val selected: Boolean,
    val onClick: (() -> Unit)?,
) : TableCell

enum class CellSize {
    Small, Medium, Large
}
