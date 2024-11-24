package parsing.objects

import parsing.{BinaryOperationNode, CellReferenceNode, NumberNode}
import parsing.traits.AstNode

object FormulaParser {
  def parse(formula: String): AstNode = {
    val tokens = tokenize(formula)
    parseExpression(tokens)
  }

  private def tokenize(formula: String): List[String] = {
    val pattern = """([A-Z]+\d+|\d+(\.\d+)?|[+\-*/()])""".r
    pattern.findAllIn(formula).toList
  }

  private def parseExpression(tokens: List[String]): AstNode = {
    val (node, remaining) = parseTerm(tokens)
    if (remaining.nonEmpty && Operators.fromSymbol(remaining.head).isDefined) {
      val operator = Operators.fromSymbol(remaining.head).get
      val right = parseExpression(remaining.tail)
      BinaryOperationNode(operator, node, right)
    } else {
      node
    }
  }

  private def parseTerm(tokens: List[String]): (AstNode, List[String]) = tokens match {
    case head :: tail if head.matches("""\d+(\.\d+)?""") =>
      (NumberNode(head.toDouble), tail)

    case head :: tail if head.matches("""[A-Z]+\d+""") =>
      val (column, row) = parseCellReference(head)
      (CellReferenceNode(column, row), tail)

    case "(" :: rest =>
      val (node, remaining) = parseExpression(rest)
      if (remaining.isEmpty || remaining.head != ")")
        throw new IllegalArgumentException("Unmatched parenthesis")
      (node, remaining.tail)

    case _ =>
      throw new IllegalArgumentException(s"Unexpected token: ${tokens.headOption.getOrElse("EOF")}")
  }
  private def parseCellReference(reference: String): (String, Int) = {
    val column = reference.takeWhile(_.isLetter)
    val row = reference.dropWhile(_.isLetter).toInt
    (column, row)
  }
}
