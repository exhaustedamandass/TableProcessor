package outputOptions
class MarkdownPrettyPrinter extends PrettyPrinter {
  override def printTable(rows: Seq[(Int, Seq[String])], headers: Option[Seq[String]], ignoredSeparator: String): String = {
    val hasHeaders = headers.isDefined

    // If headers are present, prepend a column for row indices to the data rows
    val allDataRows = if (hasHeaders) {
      rows.map { case (idx, cols) => idx.toString +: cols }
    } else {
      rows.map { case (_, cols) => cols }
    }

    // Construct a combined set of rows that includes headers if present
    val allRows = if (hasHeaders) {
      // Headers: prepend an empty cell for the row index column
      val hdr = "" +: headers.get
      hdr +: allDataRows
    } else {
      // No headers: create a fake header row of spaces (equal to the number of columns)
      val colCount = allDataRows.headOption.map(_.length).getOrElse(0)
      Seq(Seq.fill(colCount)(" ")) ++ allDataRows
    }

    val colCount = allRows.headOption.map(_.length).getOrElse(0)
    // Determine maxLen for each column (max content length)
    val maxLens = (0 until colCount).map { colIndex =>
      allRows.map(row => row(colIndex).length).maxOption.getOrElse(0)
    }

    // Each column width = maxLen + 2 (one space on each side)
    val colWidths = maxLens.map(_ + 2)

    def formatCell(content: String, width: Int): String = {
      // width includes the 2 spaces
      // One leading space + content + trailing spaces until total length = width
      val contentLen = content.length
      val used = 1 + contentLen // 1 for leading space + content length
      val needed = width - used // trailing spaces needed (at least 1)
      " " + content + (" " * needed)
    }

    // Build header row, divider row, and data rows
    // First row in allRows is either headers or a fake empty header
    val (headerRow, dividerRow, dataRows) =
    if (hasHeaders) {
      // With headers: first row is actual headers (including the empty cell for row index column)
      val hdrRow = allRows.head
      val dataOnly = allRows.tail

      val hdrCells = hdrRow.zip(colWidths).map { case (h, w) => formatCell(h, w) }
      val hdrLine = "|" + hdrCells.mkString("|") + "|"

      // Divider: for each column width w, print w dashes
      val divCells = colWidths.map(w => "-" * (w))
      val divLine = "|" + divCells.mkString("|") + "|"

      val dataLines = dataOnly.map { row =>
        val cells = row.zip(colWidths).map { case (c, w) => formatCell(c, w) }
        "|" + cells.mkString("|") + "|"
      }

      (hdrLine, divLine, dataLines)
    } else {
      // No headers: first row is the fake header row of spaces
      val fakeHdrRow = allRows.head
      val dataOnly = allRows.tail

      val hdrCells = fakeHdrRow.zip(colWidths).map { case (h, w) => formatCell(h, w) }
      val hdrLine = "|" + hdrCells.mkString("|") + "|"

      val divCells = colWidths.map(w => "-" * w)
      val divLine = "|" + divCells.mkString("|") + "|"

      val dataLines = dataOnly.map { row =>
        val cells = row.zip(colWidths).map { case (c, w) => formatCell(c, w) }
        "|" + cells.mkString("|") + "|"
      }

      (hdrLine, divLine, dataLines)
    }

    (Seq(headerRow, dividerRow) ++ dataRows).mkString("\n")
  }
}



