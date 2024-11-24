package parsing

import parsing.objects.FormulaParser
import parsing.traits.AstNode

class FormulaEvaluator(table: List[List[String]]) {
  private val visitedCells = scala.collection.mutable.Set[(String, Int)]()

  def evaluate(node: AstNode): Double = node match {
    case NumberNode(value) => value

    case CellReferenceNode(column, row) =>
      val colIndex = columnToIndex(column)
      if (row < 1 || row > table.length || colIndex < 0 || colIndex >= table.head.length) {
        throw new IllegalArgumentException(s"Invalid cell reference: $column$row")
      }
      val cellValue = table(row - 1)(colIndex)

      if (visitedCells.contains((column, row))) {
        throw new IllegalArgumentException(s"Circular reference detected at $column$row")
      }

      visitedCells.add((column, row))

      if (cellValue.startsWith("=")) {
        evaluate(FormulaParser.parse(cellValue.substring(1)))
      } else if (cellValue.isEmpty) {
        throw new IllegalArgumentException(s"Empty cell reference: $column$row")
      } else {
        cellValue.toDouble
      }

    case BinaryOperationNode(operator, left, right) =>
      val leftValue = evaluate(left)
      val rightValue = evaluate(right)
      operator(leftValue, rightValue)
  }

  private def columnToIndex(column: String): Int =
    column.foldLeft(0)((acc, c) => acc * 26 + (c - 'A' + 1)) - 1
}
