package com.kamilh.volleyballstats.presentation.features

data class TableContent(
    val rows: List<TableRow> = emptyList(),
    val header: TableRow? = null,
) {
    init {
        val rowsSizes = rows.map { it.cells.size }.toSet()
        if (rowsSizes.isNotEmpty() && header != null && header.cells.size != rowsSizes.first()) {
            error("rowSizes: $rowsSizes, header.cells.size: ${header.cells.size}")
        }
    }
}

data class TableRow(
    val cells: List<TableCell> = emptyList(),
    val isSelected: Boolean = false,
)

data class TableCell(
    val content: String,
    val size: Size,
) {
    enum class Size {
        Small, Medium, Big
    }
}
