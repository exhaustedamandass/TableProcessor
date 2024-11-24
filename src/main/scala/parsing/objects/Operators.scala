package parsing.objects

import parsing.traits.Operator

object Operators {
  case object Add extends Operator {
    val symbol: String = "+"

    def apply(left: Double, right: Double): Double = left + right
  }

  case object Subtract extends Operator {
    val symbol: String = "-"

    def apply(left: Double, right: Double): Double = left - right
  }

  case object Multiply extends Operator {
    val symbol: String = "*"

    def apply(left: Double, right: Double): Double = left * right
  }

  case object Divide extends Operator {
    val symbol: String = "/"

    def apply(left: Double, right: Double): Double =
      if (right == 0) throw new ArithmeticException("Division by zero")
      else left / right
  }

  val allOperators: List[Operator] = List(Add, Subtract, Multiply, Divide)

  def fromSymbol(symbol: String): Option[Operator] =
    allOperators.find(_.symbol == symbol)
}
