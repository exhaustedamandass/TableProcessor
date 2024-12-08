package prettyPrinting

class CSVPrettyPrinter extends PrettyPrinter {
  override def printTable(rows: Seq[(Int, Seq[String])], headers: Option[Seq[String]], separator: String = ","): String = {
    val headerRow = headers.map(h => separator + h.mkString(separator)).getOrElse("")
    val dataRows = rows.map {
      case (rowIndex, row) if headers.isDefined => rowIndex + separator + row.mkString(separator)
      case (_, row)                             => row.mkString(separator) // Exclude rowIndex
    }
    (if (headerRow.nonEmpty) Seq(headerRow) else Seq.empty) ++ dataRows mkString "\n"
  }
}

