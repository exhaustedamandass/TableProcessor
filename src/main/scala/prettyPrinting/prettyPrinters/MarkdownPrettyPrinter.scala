package prettyPrinting.prettyPrinters

import prettyPrinting.PrettyPrinter

class MarkdownPrettyPrinter extends PrettyPrinter {
  override def printTable(rows: Seq[(Int, Seq[String])], headers: Option[Seq[String]], ignoredSeparator: String): String = {
    val hasHeaders = headers.isDefined

    // If headers are present, prepend a column for row indices to the data rows
    val allDataRows = rows.map { case (idx, cols) =>
      if (hasHeaders) idx.toString +: cols else cols
    }

    // Construct a combined set of rows that includes headers if present
    val allRows = if (hasHeaders) {
      val hdr = "" +: headers.get
      hdr +: allDataRows
    } else {
      val colCount = allDataRows.headOption.map(_.length).getOrElse(0)
      Seq(Seq.fill(colCount)(" ")) ++ allDataRows
    }

    val colCount = allRows.headOption.map(_.length).getOrElse(0)

    // Determine the maximum content length for each column
    val maxLens = (0 until colCount).map { colIndex =>
      allRows.map(row => row(colIndex).length).maxOption.getOrElse(0)
    }

    // Each column width includes padding (one space on each side)
    val colWidths = maxLens.map(_ + 2)

    def formatCell(content: String, width: Int, isHeader: Boolean, isFirstRowOrColumn: Boolean): String = {
      if(hasHeaders && isFirstRowOrColumn){
        " " + content + " " * (width - content.length - 2) + " "
        //" " * (width - content.length - 1) + content + " "
      }else{
        " " * (width - content.length - 1) + content + " "
      }
    }

    def formatRow(row: Seq[String], widths: Seq[Int], isHeader: Boolean, isFirstRow: Boolean): String = {
      "|" + row.zipWithIndex.zip(widths).map {
        case ((cell, colIndex), width) =>
          val isFirstRowOrColumn = isFirstRow || colIndex == 0
          formatCell(cell, width, isHeader, isFirstRowOrColumn)
      }.mkString("|") + "|"
    }

    def createDivider(widths: Seq[Int]): String =
      "|" + widths.map("-" * _).mkString("|") + "|"

    // Generate header row, divider row, and data rows
    val (headerRow, dividerRow) =
      if (hasHeaders) {
        val hdrRow = formatRow(allRows.head, colWidths, isHeader = true, isFirstRow = true)
        val divRow = createDivider(colWidths)
        (hdrRow, divRow)
      } else {
        val fakeHdrRow = formatRow(allRows.head, colWidths, isHeader = false, isFirstRow = true)
        val divRow = createDivider(colWidths)
        (fakeHdrRow, divRow)
      }

    val dataRows = allRows.tail.zipWithIndex.map { case (row, idx) =>
      formatRow(row, colWidths, isHeader = false, isFirstRow = idx == 0)
    }

    (Seq(headerRow, dividerRow) ++ dataRows).mkString("\n")
  }
}
