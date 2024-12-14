package operator.filteringOperators

import operator.FilterOperator

object GreaterThanOperator extends FilterOperator {
  def apply(cellValue: Double, filterValue: Double): Boolean = cellValue > filterValue
}
