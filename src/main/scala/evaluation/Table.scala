package evaluation

import operator.OperatorRegistry
import parsing.FormulaParser
import parsing.operatorRegistry.DefaultArithmeticOperatorRegistry

import scala.collection.mutable

class Table(
             rows: Int,
             cols: Int,
             operatorRegistry: OperatorRegistry[Double, Double] = DefaultArithmeticOperatorRegistry
           ) {
  private val data: mutable.Map[String, Cell] = mutable.Map()

  def setCell(col: String, row: Int, value: String): Unit = {
    data(cellKey(col, row)) = Cell(value)
  }

  def getCell(col: String, row: Int): Option[Cell] = data.get(cellKey(col, row))

  def getCellValue(col: String, row: Int): String = {
    getCell(col, row) match {
      case Some(Cell(value)) if value.startsWith("=") =>
        evaluateFormulaCell(value.tail)

      case Some(Cell(value)) =>
        // Plain value cell
        value

      case None =>
        // Empty cell
        ""
    }
  }

  private def cellKey(col: String, row: Int): String = s"$col$row"

  private def evaluateFormulaCell(formula: String): String = {
    val parser = new FormulaParser(operatorRegistry)
    val evaluator = FormulaEvaluator

    parser.parse(formula) match {
      case ast =>
        evaluator.evaluate(ast, this, operatorRegistry = operatorRegistry) match {
          case Right(result) => result.toString
          case Left(error)   => s"<ERROR: $error>"
        }
    }
  }
}


