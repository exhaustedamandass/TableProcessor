package evaluation

import operator.OperatorRegistry
import parsing._
import parsing.operatorRegistry.DefaultArithmeticOperatorRegistry

object FormulaEvaluator {
  def evaluate(
                ast: AST,
                table: Table,
                visited: Set[String] = Set(),
                operatorRegistry: OperatorRegistry[Double, Double] = DefaultArithmeticOperatorRegistry
              ): Either[String, Double] = ast match {
    case Number(value) =>
      Right(value)

    case Reference(col, row) =>
      val cellKey = s"$col$row"

      // Detect cyclical dependency
      if (visited.contains(cellKey)) {
        return Left(s"Cyclical dependency detected at $cellKey")
      }

      val cellValue = table.getCell(col, row).map(_.value).getOrElse("")

      if (cellValue.isEmpty) {
        Left(s"Referenced empty cell at $col$row")
      } else if (cellValue.startsWith("=")) {
        // Recursively evaluate the formula in the referenced cell
        val formula = cellValue.tail
        val parser = new FormulaParser(operatorRegistry)
        try {
          val referencedAST = parser.parse(formula)
          evaluate(referencedAST, table, visited + cellKey, operatorRegistry)
        } catch {
          case e: RuntimeException =>
            Left(s"Parsing error in referenced cell $col$row: ${e.getMessage}")
        }
      } else {
        try {
          Right(cellValue.toDouble)
        } catch {
          case _: NumberFormatException => Left(s"Invalid numeric value at $col$row: $cellValue")
        }
      }

    case BinaryOp(operator, left, right) =>
      (evaluate(left, table, visited, operatorRegistry), evaluate(right, table, visited, operatorRegistry)) match {
        case (Right(lhs), Right(rhs)) =>
          try {
            Right(operatorRegistry.evaluate(operator, lhs, rhs))
          } catch {
            case _: ArithmeticException =>
              Left(s"Arithmetic error with operator $operator on values ($lhs, $rhs)")
          }
        case (Left(error), _) => Left(error)
        case (_, Left(error)) => Left(error)
      }
  }
}