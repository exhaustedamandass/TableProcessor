package evaluation

import org.scalatest.funsuite.AnyFunSuite
import parsing.{FormulaParser, Number, Reference}
import parsing.operatorRegistry.DefaultArithmeticOperatorRegistry

class FormulaEvaluatorTest extends AnyFunSuite {

  private val operatorRegistry = DefaultArithmeticOperatorRegistry

  test("Evaluate a simple number AST") {
    val table = new Table(10, 10, operatorRegistry)
    val ast = Number(42.0)
    val result = FormulaEvaluator.evaluate(ast, table, operatorRegistry = operatorRegistry)
    assert(result.isRight, s"Expected Right(42.0), got $result")
    assert(result == Right(42.0), s"Expected Right(42.0), got $result")
  }

  test("Evaluate a reference to a numeric cell") {
    val table = new Table(10, 10, operatorRegistry)
    table.setCell("A", 1, "123")
    val ast = Reference("A",1)
    val result = FormulaEvaluator.evaluate(ast, table, operatorRegistry = operatorRegistry)
    assert(result.isRight, s"Expected Right(123.0), got $result")
    assert(result == Right(123.0), s"Expected Right(123.0), got $result")
  }

  test("Evaluate a reference to an empty cell") {
    val table = new Table(10, 10, operatorRegistry)
    // No cell set means empty
    val ast = Reference("A",1)
    val result = FormulaEvaluator.evaluate(ast, table, operatorRegistry = operatorRegistry)
    assert(result.isLeft, s"Expected Left(...) because cell is empty, got $result")
    assert(result.left.get.contains("Referenced empty cell"), s"Expected error about empty cell, got $result")
  }

  test("Evaluate a reference to a cell with invalid numeric value") {
    val table = new Table(10, 10, operatorRegistry)
    table.setCell("A",1,"abc") // Invalid numeric
    val ast = Reference("A",1)
    val result = FormulaEvaluator.evaluate(ast, table, operatorRegistry = operatorRegistry)
    assert(result.isLeft, s"Expected Left(...) for invalid number, got $result")
    assert(result.left.get.contains("Invalid numeric value"), s"Expected invalid numeric error, got $result")
  }

  test("Evaluate a simple formula reference") {
    // A1 = "2", B1 = "=A1+3"
    val table = new Table(10, 10, operatorRegistry)
    table.setCell("A",1,"2")
    table.setCell("B",1,"=A1+3")
    val ast = Reference("B",1)
    val result = FormulaEvaluator.evaluate(ast, table, operatorRegistry = operatorRegistry)
    assert(result.isRight, s"Expected success, got $result")
    assert(result == Right(5.0), s"Expected 5.0, got $result")
  }

  test("Evaluate a formula with multiple references") {
    // A1 = "10", A2 = "20", B1 = "=A1+A2"
    val table = new Table(10, 10, operatorRegistry)
    table.setCell("A",1,"10")
    table.setCell("A",2,"20")
    table.setCell("B",1,"=A1+A2")
    val ast = Reference("B",1)
    val result = FormulaEvaluator.evaluate(ast, table, operatorRegistry = operatorRegistry)
    assert(result.isRight, s"Expected success, got $result")
    assert(result == Right(30.0), s"Expected 30.0, got $result")
  }

  test("Evaluate a formula with nested formulas") {
    // A1 = "2"
    // A2 = "=A1*3" -> evaluates to 6
    // B1 = "=A2+4" -> should evaluate to 10
    val table = new Table(10, 10, operatorRegistry)
    table.setCell("A",1,"2")
    table.setCell("A",2,"=A1*3")
    table.setCell("B",1,"=A2+4")
    val ast = Reference("B",1)
    val result = FormulaEvaluator.evaluate(ast, table, operatorRegistry = operatorRegistry)
    assert(result.isRight, s"Expected success, got $result")
    assert(result == Right(10.0), s"Expected 10.0, got $result")
  }

  test("Detect cyclical dependencies") {
    // A1 = "=B1"
    // B1 = "=A1"
    val table = new Table(10, 10, operatorRegistry)
    table.setCell("A",1,"=B1")
    table.setCell("B",1,"=A1")
    val ast = Reference("A",1)
    val result = FormulaEvaluator.evaluate(ast, table, operatorRegistry = operatorRegistry)
    assert(result.isLeft, s"Expected Left(...) due to cyclical dependency, got $result")
    assert(result.left.get.contains("Cyclical dependency"), s"Expected cyclical dependency error, got $result")
  }

  test("Evaluate a formula with a division by zero") {
    // A1 = "0", B1 = "=10/A1"
    val table = new Table(10, 10, operatorRegistry)
    table.setCell("A",1,"0")
    table.setCell("B",1,"=10/A1")
    val ast = Reference("B",1)
    val result = FormulaEvaluator.evaluate(ast, table, operatorRegistry = operatorRegistry)
    assert(result.isLeft, s"Expected Left(...) due to division by zero, got $result")
    assert(result.left.get.contains("Arithmetic error"), s"Expected arithmetic error, got $result")
  }

  test("Evaluate a formula with unknown operator in the referenced cell") {
    // '^' not registered
    // A1 = "2", B1 = "=A1^3"
    val table = new Table(10, 10, operatorRegistry)
    table.setCell("A",1,"2")
    table.setCell("B",1,"=A1^3")
    val ast = Reference("B",1)
    val result = FormulaEvaluator.evaluate(ast, table, operatorRegistry = operatorRegistry)
    // Parsing this should fail at parser stage due to '^'
    assert(result.isLeft, s"Expected Left(...) due to unknown operator '^', got $result")
    assert(result.left.get.contains("Unexpected token: ^") || result.left.get.contains("Unexpected"),
      s"Expected parsing error mentioning '^', got $result")
  }

  test("Evaluate a complex numeric formula without references") {
    // Evaluate directly: (10+20)/(5-1) = 30/4 = 7.5
    val table = new Table(10, 10, operatorRegistry)
    val parser = new FormulaParser(operatorRegistry)
    val ast = parser.parse("(10+20)/(5-1)")
    val result = FormulaEvaluator.evaluate(ast, table, operatorRegistry = operatorRegistry)
    assert(result.isRight, s"Expected success, got $result")
    assert(result == Right(7.5), s"Expected 7.5, got $result")
  }

  test("Evaluate a reference to a formula that references another formula") {
    // A1 = "4"
    // A2 = "=A1*5" -> 20
    // B2 = "=A2-10" -> 10
    val table = new Table(10, 10, operatorRegistry)
    table.setCell("A",1,"4")
    table.setCell("A",2,"=A1*5")
    table.setCell("B",2,"=A2-10")
    val ast = Reference("B",2)
    val result = FormulaEvaluator.evaluate(ast, table, operatorRegistry = operatorRegistry)
    assert(result.isRight, s"Expected success, got $result")
    assert(result == Right(10.0), s"Expected 10.0, got $result")
  }
}
