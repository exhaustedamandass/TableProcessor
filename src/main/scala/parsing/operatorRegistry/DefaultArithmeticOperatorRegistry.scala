package parsing.operatorRegistry

import operator.OperatorRegistry
import operator.arithmeticOperators.{AddOperator, DivOperator, MulOperator, SubOperator}

// Default registry for arithmetic operations
object DefaultArithmeticOperatorRegistry extends OperatorRegistry[Double, Double] {
  register("+", AddOperator)
  register("-", SubOperator)
  register("*", MulOperator)
  register("/", DivOperator)
}
