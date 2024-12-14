package operator.filteringOperators

import operator.FilterOperator

object GreaterOrEqualOperator extends FilterOperator {
  def apply(cellValue: Double, filterValue: Double): Boolean = cellValue >= filterValue
}
