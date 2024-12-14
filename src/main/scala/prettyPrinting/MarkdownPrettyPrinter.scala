package prettyPrinting

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

    def formatCell(content: String, width: Int): String =
      " " + content.padTo(width - 1, ' ') // Adds leading and trailing spaces

    def formatRow(row: Seq[String], widths: Seq[Int]): String =
      "|" + row.zip(widths).map { case (cell, width) => formatCell(cell, width) }.mkString("|") + "|"

    def createDivider(widths: Seq[Int]): String =
      "|" + widths.map("-" * _).mkString("|") + "|"

    // Generate header row, divider row, and data rows
    val (headerRow, dividerRow) =
      if (hasHeaders) {
        val hdrRow = formatRow(allRows.head, colWidths)
        val divRow = createDivider(colWidths)
        (hdrRow, divRow)
      } else {
        val fakeHdrRow = formatRow(allRows.head, colWidths)
        val divRow = createDivider(colWidths)
        (fakeHdrRow, divRow)
      }

    val dataRows = allRows.tail.map(row => formatRow(row, colWidths))

    (Seq(headerRow, dividerRow) ++ dataRows).mkString("\n")
  }
}



