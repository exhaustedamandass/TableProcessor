package ranges

class DefaultRangeSelector extends RangeSelector {
  override def calculateRange(from: String, to: String, maxRows: Int, maxCols: Int): (Int, Int, Int, Int) = {
    val fromCol = from.head - 'A'
    val fromRow = from.tail.toInt - 1
    val toCol = to.head - 'A'
    val toRow = to.tail.toInt - 1

    val startRow = math.min(fromRow, toRow).clamp(0, maxRows - 1)
    val endRow = math.max(fromRow, toRow).clamp(0, maxRows - 1)
    val startCol = math.min(fromCol, toCol).clamp(0, maxCols - 1)
    val endCol = math.max(fromCol, toCol).clamp(0, maxCols - 1)

    (startRow, endRow, startCol, endCol)
  }

  implicit class IntOps(value: Int) {
    def clamp(min: Int, max: Int): Int = math.max(min, math.min(max, value))
  }
}

