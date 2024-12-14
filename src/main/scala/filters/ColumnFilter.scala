package filters

import filters.operatorRegistry.DefaultFilterOperatorRegistry
import operator.OperatorRegistry

case class ColumnFilter(
                         column: String,
                         operatorName: String,
                         value: Double,
                         operatorRegistry: OperatorRegistry[Double, Boolean] = DefaultFilterOperatorRegistry
                       ) extends Filter {

  override def apply(row: Map[String, String]): Boolean = {
    row.get(column) match {
      case Some(cellValue) if cellValue.nonEmpty =>
        try {
          val cellValueAsDouble = cellValue.toDouble
          operatorRegistry.evaluate(operatorName, cellValueAsDouble, value)
        } catch {
          case _: NumberFormatException => false
        }
      case _ => false
    }
  }
}
