package operator.filteringOperators

import operator.FilterOperator

object EqualOperator extends FilterOperator {
  def apply(cellValue: Double, filterValue: Double): Boolean = cellValue == filterValue
}
