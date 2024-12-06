package parsing

import parsing.objects.{FormulaParser, Operators, Table}
import parsing.traits.{AST, BinaryOp, Number, Reference}

object FormulaEvaluator {
  def evaluate(ast: AST, table: Table, visited: Set[String] = Set()): Either[String, Double] = ast match {
    case Number(value) => Right(value)

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
        evaluate(FormulaParser.parse(cellValue.tail), table, visited + cellKey)
      } else {
        try {
          Right(cellValue.toDouble)
        } catch {
          case _: NumberFormatException => Left(s"Invalid value at $col$row: $cellValue")
        }
      }

    case BinaryOp(operator, left, right) =>
      (evaluate(left, table, visited), evaluate(right, table, visited)) match {
        case (Right(lhs), Right(rhs)) =>
          try {
            Right(Operators.ops(operator)(lhs, rhs))
          } catch {
            case _: ArithmeticException => Left(s"Arithmetic error with operator $operator")
          }
        case (Left(error), _) => Left(error)
        case (_, Left(error)) => Left(error)
      }
  }
}