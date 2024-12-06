package filters

case class ColumnFilter(column: String, operator: String, value: Double) extends Filter {
  override def apply(row: Map[String, String]): Boolean = {
    row.get(column) match {
      case Some(cellValue) if cellValue.nonEmpty =>
        try {
          val cellValueAsDouble = cellValue.toDouble
          operator match {
            case "<"  => cellValueAsDouble < value
            case ">"  => cellValueAsDouble > value
            case "<=" => cellValueAsDouble <= value
            case ">=" => cellValueAsDouble >= value
            case "==" => cellValueAsDouble == value
            case "!=" => cellValueAsDouble != value
            case _    => throw new IllegalArgumentException(s"Invalid operator: $operator")
          }
        } catch {
          case _: NumberFormatException => false // Non-numeric cells do not satisfy the filter
        }
      case _ => false // Empty or non-existent cells do not satisfy the filter
    }
  }
}