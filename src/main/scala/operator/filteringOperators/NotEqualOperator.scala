package operator.filteringOperators

import operator.FilterOperator

object NotEqualOperator extends FilterOperator {
  def apply(cellValue: Double, filterValue: Double): Boolean = cellValue != filterValue
}
