package filters

import traits.Filter

import scala.util.Try

class ValueFilter(column: String, operator: String, value: Double) extends Filter {
  override def filter(table: List[List[String]]): List[List[String]] = {
    val colIndex = table.head.indexOf(column)
    if (colIndex == -1) throw new IllegalArgumentException(s"Column $column does not exist.")

    table.filter { row =>
      Try(row(colIndex).toDouble).toOption.exists { cellValue =>
        operator match {
          case "<"  => cellValue < value
          case ">"  => cellValue > value
          case "<=" => cellValue <= value
          case ">=" => cellValue >= value
          case "==" => cellValue == value
          case "!=" => cellValue != value
          case _    => throw new IllegalArgumentException(s"Invalid operator: $operator")
        }
      }
    }
  }
}
