package Headers

trait HeaderHandler {
  def generateHeaders(startCol: Int, endCol: Int): String
  def formatRow(rowNumber: Int, row: Seq[String]): String
}

