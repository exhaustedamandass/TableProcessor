package ranges

class DefaultRangeSelector extends RangeSelector {

  override def calculateRange(from: String, to: String, maxRows: Int, maxCols: Int): GridRange = {
    // Extract column and row parts
    val (fromColName, fromRowNum) = splitColumnRow(from)
    val (toColName, toRowNum) = splitColumnRow(to)

    val fromCol = columnNameToIndex(fromColName)
    val fromRow = fromRowNum - 1 // zero-based index
    val toCol = columnNameToIndex(toColName)
    val toRow = toRowNum - 1     // zero-based index

    // Determine start and end indices for rows and columns, clamped within limits
    val (startRow, endRow) = calculateBounds(fromRow, toRow, maxRows)
    val (startCol, endCol) = calculateBounds(fromCol, toCol, maxCols)

    // Return a named case class instead of a tuple
    GridRange(startRow, endRow, startCol, endCol)
  }

  private def splitColumnRow(cellRef: String): (String, Int) = {
    val colPart = cellRef.takeWhile(_.isLetter)
    val rowPart = cellRef.dropWhile(_.isLetter)
    (colPart, rowPart.toInt)
  }

  private def columnNameToIndex(colName: String): Int = {
    // Convert a base-26 column name (A-Z, AA, AB, etc.) to a zero-based index
    colName.foldLeft(0) { (acc, char) =>
      acc * 26 + (char - 'A' + 1)
    } - 1 // make zero-based
  }

  private def calculateBounds(minValue: Int, maxValue: Int, limit: Int): (Int, Int) = {
    val start = clamp(math.min(minValue, maxValue), 0, limit - 1)
    val end = clamp(math.max(minValue, maxValue), 0, limit - 1)
    (start, end)
  }

  private def clamp(value: Int, min: Int, max: Int): Int = {
    math.max(min, math.min(max, value))
  }
}
