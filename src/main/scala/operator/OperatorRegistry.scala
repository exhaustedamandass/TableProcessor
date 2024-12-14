package operator

import scala.collection.mutable

// Generic registry
class OperatorRegistry[I, O] {
  private val operators = mutable.Map[String, Operator[I, O]]()

  def register(name: String, operator: Operator[I, O]): Unit = {
    operators += (name -> operator)
  }

  def get(name: String): Option[Operator[I, O]] = operators.get(name)

  def evaluate(name: String, lhs: I, rhs: I): O = {
    operators.get(name) match {
      case Some(operator) => operator(lhs, rhs)
      case None => throw new IllegalArgumentException(s"Invalid operator: $name")
    }
  }

  def precedence(name: String): Int = {
    operators.get(name) match {
      case Some(op: ArithmeticOperator) => op.precedence
      case _ => throw new IllegalArgumentException(s"Operator not found or not arithmetic: $name")
    }
  }
}

