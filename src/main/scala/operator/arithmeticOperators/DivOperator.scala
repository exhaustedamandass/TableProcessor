package operator.arithmeticOperators

import operator.ArithmeticOperator

object DivOperator extends ArithmeticOperator {
  def apply(lhs: Double, rhs: Double): Double = {
    if (rhs == 0.0) {
      throw new ArithmeticException("Division by zero")
    }
    lhs / rhs
  }
  def precedence: Int = 2
}
