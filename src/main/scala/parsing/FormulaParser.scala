package parsing

import scala.collection.mutable

object FormulaParser {
  def parse(formula: String): AST = {
    val tokens = tokenize(formula)
    buildAST(tokens)
  }

  private def tokenize(formula: String): List[String] = {
    val pattern = "([A-Z]+\\d+|\\d+(\\.\\d+)?|[+\\-*/()])".r
    pattern.findAllIn(formula).toList
  }

  private def buildAST(tokens: List[String]): AST = {
    val output = mutable.Stack[AST]()
    val operators = mutable.Stack[String]()

    def precedence(op: String): Int = Operators.ops(op).precedence

    var idx = 0
    while (idx < tokens.length) {
      val token = tokens(idx)
      token match {
        case token if token.matches("\\d+(\\.\\d+)?") =>
          output.push(Number(token.toDouble))
        case token if token.matches("[A-Z]+\\d+") =>
          val col = token.filter(_.isLetter)
          val row = token.filter(_.isDigit).toInt
          output.push(Reference(col, row))
        case token if Operators.ops.contains(token) =>
          while (operators.nonEmpty && precedence(operators.top) >= precedence(token)) {
            val op = operators.pop()
            val right = output.pop()
            val left = output.pop()
            output.push(BinaryOp(op, left, right))
          }
          operators.push(token)
        case "(" =>
          operators.push(token)
        case ")" =>
          while (operators.nonEmpty && operators.top != "(") {
            val op = operators.pop()
            val right = output.pop()
            val left = output.pop()
            output.push(BinaryOp(op, left, right))
          }
          if (operators.isEmpty || operators.pop() != "(") {
            throw new RuntimeException("Mismatched parentheses")
          }
        case _ =>
          throw new RuntimeException(s"Unexpected token: $token")
      }
      idx += 1
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
