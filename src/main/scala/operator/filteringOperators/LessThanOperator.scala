package operator.filteringOperators

import operator.FilterOperator

object LessThanOperator extends FilterOperator {
  def apply(cellValue: Double, filterValue: Double): Boolean = cellValue < filterValue
}
