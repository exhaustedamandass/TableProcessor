package tableProcessor

import scala.annotation.tailrec

object ColumnNameConverter {
  // Helper method to convert a zero-based column index to a spreadsheet-style column name (A, B, ..., AA, AB...)
  def columnIndexToName(index: Int): String = {
    @tailrec
    def loop(i: Int, acc: List[Char]): List[Char] = {
      if (i < 0) acc
      else {
        val remainder = i % 26
        val letter = (remainder + 'A').toChar
        val next = (i / 26) - 1
        loop(next, letter :: acc)
      }
    }
    loop(index, Nil).mkString
  }
}
