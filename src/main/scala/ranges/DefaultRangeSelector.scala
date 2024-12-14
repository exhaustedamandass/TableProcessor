package ranges

class DefaultRangeSelector extends RangeSelector {
  //TODO: give name to the return value
  //TODO: make some class for 4 ints
  //TODO: column name can be double char and more like "AA" or "AZ"
  override def calculateRange(from: String, to: String, maxRows: Int, maxCols: Int): (Int, Int, Int, Int) = {
    val fromCol = from.head - 'A'
    val fromRow = from.tail.toInt - 1
    val toCol = to.head - 'A'
    val toRow = to.tail.toInt - 1

    def calculateBounds(minValue: Int, maxValue: Int, limit: Int): (Int, Int) = {
      val start = math.min(minValue, maxValue).clamp(0, limit - 1)
      val end = math.max(minValue, maxValue).clamp(0, limit - 1)
      (start, end)
    }

    val (startRow, endRow) = calculateBounds(fromRow, toRow, maxRows)
    val (startCol, endCol) = calculateBounds(fromCol, toCol, maxCols)

    (startRow, endRow, startCol, endCol)
  }

  implicit class IntOps(value: Int) {
    def clamp(min: Int, max: Int): Int = math.max(min, math.min(max, value))
  }
}

