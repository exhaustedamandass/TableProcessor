package filters.operatorRegistry

import operator.OperatorRegistry
import operator.filteringOperators.{EqualOperator, GreaterOrEqualOperator, GreaterThanOperator, LessOrEqualOperator, LessThanOperator, NotEqualOperator}

// Default registry for filter operators
object DefaultFilterOperatorRegistry extends OperatorRegistry[Double, Boolean] {
  register("<", LessThanOperator)
  register(">", GreaterThanOperator)
  register("<=", LessOrEqualOperator)
  register(">=", GreaterOrEqualOperator)
  register("==", EqualOperator)
  register("!=", NotEqualOperator)
}
