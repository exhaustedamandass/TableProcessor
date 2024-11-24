package filters

import traits.Filter

class FilterIsNotEmpty(column: String) extends Filter {
  override def filter(table: List[List[String]]): List[List[String]] = {
    val colIndex = table.head.indexOf(column)
    if (colIndex == -1) throw new IllegalArgumentException(s"Column $column does not exist.")

    table.filter { row =>
      row(colIndex).nonEmpty
    }
  }
}
