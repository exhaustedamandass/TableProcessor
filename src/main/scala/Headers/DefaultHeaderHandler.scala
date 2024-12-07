package Headers

class DefaultHeaderHandler extends HeaderHandler {
  override def generateHeaders(startCol: Int, endCol: Int): String = {
    val headers = (startCol to endCol).map(i => ('A' + i).toChar.toString)
    ", " + headers.mkString(", ")
  }

  override def formatRow(rowNumber: Int, row: Seq[String]): String = {
    rowNumber + ", " + row.mkString(", ")
  }
}

