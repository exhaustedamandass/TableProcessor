package operator.arithmeticOperators

import operator.ArithmeticOperator

object AddOperator extends ArithmeticOperator {
  def apply(lhs: Double, rhs: Double): Double = lhs + rhs
  def precedence: Int = 1
}
