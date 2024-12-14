package operator.filteringOperators

import operator.FilterOperator

object LessOrEqualOperator extends FilterOperator {
  def apply(cellValue: Double, filterValue: Double): Boolean = cellValue <= filterValue
}
