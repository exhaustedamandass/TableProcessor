package parsing.objects

import parsing.FormulaEvaluator

import scala.collection.mutable

class Table(rows: Int, cols: Int) {
  val data: mutable.Map[String, Cell] = mutable.Map()

  def setCell(col: String, row: Int, value: String): Unit = {
    data(s"$col$row") = Cell(value)
  }

  def getCell(col: String, row: Int): Option[Cell] = data.get(s"$col$row")

  def getCellValue(col: String, row: Int): String = {
    data.get(s"$col$row") match {
      case Some(Cell(value)) if value.startsWith("=") =>
        FormulaEvaluator.evaluate(FormulaParser.parse(value.tail), this) match {
          case Right(result) => result.toString
          case Left(error)   => s"<ERROR: $error>"
        }
      case Some(Cell(value)) => value
      case None => "" // Empty cell
    }
  }
}

// A helper class to store a cell's content
case class Cell(value: String)


