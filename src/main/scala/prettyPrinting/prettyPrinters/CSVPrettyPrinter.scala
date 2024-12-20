package prettyPrinting.prettyPrinters

import prettyPrinting.PrettyPrinter

class CSVPrettyPrinter extends PrettyPrinter {
  override def printTable(rows: Seq[(Int, Seq[String])], headers: Option[Seq[String]], separator: String = ","): String = {
    val hasHeaders = headers.isDefined

    // Combine headers and rows for width calculation
    val allRows = if (hasHeaders) {
      // If headers exist, prepend an empty string for the "index" column in the headers
      val hdr = "" +: headers.get
      // For data rows, prepend the index as a string
      val dataRows = rows.map { case (idx, cols) => idx.toString +: cols }
      hdr +: dataRows
    } else {
      // If no headers, do not prepend the index column at all
      rows.map { case (_, cols) => cols }
    }

    if (allRows.isEmpty) return ""

    // Calculate column widths based on the maximum length in each column
    val colCount = allRows.head.length
    val baseWidths = (0 until colCount).map { colIndex =>
      allRows.map(row => row(colIndex).length).max
    }

    // Add space for the trailing comma and space in all but the last column
    val colWidths = baseWidths.zipWithIndex.map { case (w, i) =>
      if (i < colCount - 1) w + 2 else w
    }

    // Format a single cell
    def formatCell(
                    cell: String,
                    width: Int,
                    isLast: Boolean,
                    isFirst: Boolean,
                    isFirstRow: Boolean,
                  ): String = {
      if (!isLast && (isFirst || isFirstRow) && hasHeaders) {
        // First column: cell value, followed by separator, then spaces
        val remainingSpace = width - (cell.length + separator.length)
        cell + separator + " " * remainingSpace
      } else if (!isLast) {
        // For non-last columns:
        // Right-align the cell in (width - 2) space and then add separator and a space
        if (cell.nonEmpty) {
          val cellFmt = s"%${width - 2}s"
          cellFmt.format(cell) + separator + " "
        } else {
          separator + " " * (width - separator.length)
        }
      } else {
        // Last column: add right-aligned cell and handle empty cell
        if (cell.nonEmpty) {
          val fmt = s"%${width}s"
          fmt.format(cell) + separator + " "
        } else {
          separator + " " * (width - separator.length)
        }
      }
    }


    // Format each row
    def formatRow(row: Seq[String], widths: Seq[Int], isFirstRow: Boolean): String = {
      row.zip(widths).zipWithIndex.map { case ((cell, w), i) =>
        formatCell(cell, w, i == row.size - 1, i == 0, isFirstRow)
      }.mkString
    }


    // Format all rows
    val lines = allRows.zipWithIndex.map { case (row, idx) =>
      formatRow(row, colWidths, isFirstRow = (idx == 0))
    }

    lines.mkString("\n")
  }
}

