package parsing

import operator.OperatorRegistry

import scala.collection.mutable

class FormulaParser(operatorRegistry: OperatorRegistry[Double, Double]) {

  def parse(formula: String): AST = {
    val tokens = tokenize(formula)
    buildAST(tokens)
  }

  private def tokenize(formula: String): List[String] = {
    val pattern = "([A-Z]+\\d+|\\d+(\\.\\d+)?|[+\\-*/()^&])".r
    pattern.findAllIn(formula).toList
  }

  private def buildAST(tokens: List[String]): AST = {
    val output = mutable.Stack[AST]()
    val operators = mutable.Stack[String]()

    def precedence(op: String): Int = operatorRegistry.precedence(op)

    def processOperator(token: String): Unit = {
      while (operators.nonEmpty && operators.top != "(" && precedence(operators.top) >= precedence(token)) {
        val op = operators.pop()
        val right = output.pop()
        val left = output.pop()
        output.push(BinaryOp(op, left, right))
      }
      operators.push(token)
    }

    def processClosingParenthesis(): Unit = {
      while (operators.nonEmpty && operators.top != "(") {
        val op = operators.pop()
        val right = output.pop()
        val left = output.pop()
        output.push(BinaryOp(op, left, right))
      }
      if (operators.isEmpty || operators.pop() != "(") {
        throw new RuntimeException("Mismatched parentheses")
      }
    }

    tokens.foreach {
      case t if t.matches("\\d+(\\.\\d+)?") =>
        output.push(Number(t.toDouble))
      case t if t.matches("[A-Z]+\\d+") =>
        val col = t.filter(_.isLetter)
        val row = t.filter(_.isDigit).toInt
        output.push(Reference(col, row))
      case t if operatorRegistry.get(t).isDefined =>
        processOperator(t)
      case "(" =>
        operators.push("(")
      case ")" =>
        processClosingParenthesis()
      case unexpected =>
        throw new RuntimeException(s"Unexpected token: $unexpected")
    }

    while (operators.nonEmpty) {
      val op = operators.pop()
      val right = output.pop()
      val left = output.pop()
      output.push(BinaryOp(op, left, right))
    }

    output.pop()
  }
}
