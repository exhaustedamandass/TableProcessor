package ranges

trait RangeSelector {
  def calculateRange(from: String, to: String, maxRows: Int, maxCols: Int): (Int, Int, Int, Int)
}
