package traits

import filters.Range
object Range {
  def parse(rangeArgs: List[String]): Range = {
    if (rangeArgs.length != 2) {
      throw new IllegalArgumentException("Invalid range format. Expected --range [FROM] [TO]")
    }

    val from = rangeArgs(0)
    val to = rangeArgs(1)

    if (!from.matches("^[A-Z]+\\d+$") || !to.matches("^[A-Z]+\\d+$")) {
      throw new IllegalArgumentException("Invalid range format. Expected cell references like B2 or D4")
    }

    val fromCol = from.filter(_.isLetter).toUpperCase.head
    val fromRow = from.filter(_.isDigit).toInt
    val toCol = to.filter(_.isLetter).toUpperCase.head
    val toRow = to.filter(_.isDigit).toInt

    filters.Range(
      fromRow = Math.min(fromRow, toRow),
      toRow = Math.max(fromRow, toRow),
      fromCol = Math.min(fromCol, toCol).toChar,
      toCol = Math.max(fromCol, toCol).toChar
    )
  }
}
