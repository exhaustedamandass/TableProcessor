package parsing

import org.scalatest.funsuite.AnyFunSuite
import parsing.operatorRegistry.DefaultArithmeticOperatorRegistry

import scala.util.Try

class FormulaParserTest extends AnyFunSuite {

  // Using the default arithmetic operator registry provided
  private val parser = new FormulaParser(DefaultArithmeticOperatorRegistry)

  private def safeParse(formula: String): Try[AST] = Try(parser.parse(formula))

  test("Parse simple number") {
    val ast = parser.parse("42")
    assert(ast.isInstanceOf[Number], s"Expected Number, got ${ast.getClass}")
    val num = ast.asInstanceOf[Number]
    assert(num.value == 42.0, s"Expected value 42.0, got ${num.value}")
  }

  test("Parse simple addition") {
    val ast = parser.parse("1+2")
    assert(ast.isInstanceOf[BinaryOp], s"Expected BinaryOp, got ${ast.getClass}")
    val op = ast.asInstanceOf[BinaryOp]
    assert(op.operator == "+", s"Expected '+', got ${op.operator}")
    assert(op.left == Number(1.0), s"Expected left to be Number(1.0), got ${op.left}")
    assert(op.right == Number(2.0), s"Expected right to be Number(2.0), got ${op.right}")
  }

  test("Parse simple reference") {
    val ast = parser.parse("A10")
    assert(ast.isInstanceOf[Reference], s"Expected Reference, got ${ast.getClass}")
    val ref = ast.asInstanceOf[Reference]
    assert(ref.col == "A", s"Expected col 'A', got ${ref.col}")
    assert(ref.row == 10, s"Expected row 10, got ${ref.row}")
  }

  test("Parse expression with multiple operators respecting precedence") {
    // 1+2*3 should parse as 1 + (2*3)
    val ast = parser.parse("1+2*3")
    assert(ast.isInstanceOf[BinaryOp], s"Expected BinaryOp, got ${ast.getClass}")
    val rootOp = ast.asInstanceOf[BinaryOp]
    assert(rootOp.operator == "+", s"Expected '+', got ${rootOp.operator}")
    assert(rootOp.left == Number(1.0), s"Expected left to be Number(1.0), got ${rootOp.left}")

    assert(rootOp.right.isInstanceOf[BinaryOp], s"Expected BinaryOp on right, got ${rootOp.right.getClass}")
    val rightOp = rootOp.right.asInstanceOf[BinaryOp]
    assert(rightOp.operator == "*", s"Expected '*', got ${rightOp.operator}")
    assert(rightOp.left == Number(2.0), s"Expected left to be Number(2.0), got ${rightOp.left}")
    assert(rightOp.right == Number(3.0), s"Expected right to be Number(3.0), got ${rightOp.right}")
  }

  test("Parse expression with parentheses") {
    // (1+2)*3 => * ( + (1,2) , 3 )
    val ast = parser.parse("(1+2)*3")
    assert(ast.isInstanceOf[BinaryOp], s"Expected BinaryOp at root, got ${ast.getClass}")
    val rootOp = ast.asInstanceOf[BinaryOp]
    assert(rootOp.operator == "*", s"Expected '*', got ${rootOp.operator}")

    assert(rootOp.left.isInstanceOf[BinaryOp], s"Expected BinaryOp on left, got ${rootOp.left.getClass}")
    val leftOp = rootOp.left.asInstanceOf[BinaryOp]
    assert(leftOp.operator == "+", s"Expected '+', got ${leftOp.operator}")
    assert(leftOp.left == Number(1.0), s"Expected left operand of + is 1.0, got ${leftOp.left}")
    assert(leftOp.right == Number(2.0), s"Expected right operand of + is 2.0, got ${leftOp.right}")

    assert(rootOp.right == Number(3.0), s"Expected right operand of * is 3.0, got ${rootOp.right}")
  }

  test("Parse reference and number combination") {
    // A1+B2
    val ast = parser.parse("A1+B2")
    assert(ast.isInstanceOf[BinaryOp], s"Expected BinaryOp, got ${ast.getClass}")
    val op = ast.asInstanceOf[BinaryOp]
    assert(op.operator == "+", s"Expected '+', got ${op.operator}")

    assert(op.left.isInstanceOf[Reference], s"Expected Reference on left, got ${op.left.getClass}")
    val leftRef = op.left.asInstanceOf[Reference]
    assert(leftRef.col == "A" && leftRef.row == 1, s"Expected Reference(A,1), got $leftRef")

    assert(op.right.isInstanceOf[Reference], s"Expected Reference on right, got ${op.right.getClass}")
    val rightRef = op.right.asInstanceOf[Reference]
    assert(rightRef.col == "B" && rightRef.row == 2, s"Expected Reference(B,2), got $rightRef")
  }

  test("Parse complex reference formula") {
    // (A1+ B2)*C10
    val ast = parser.parse("(A1+B2)*C10")
    assert(ast.isInstanceOf[BinaryOp], s"Expected BinaryOp, got ${ast.getClass}")
    val rootOp = ast.asInstanceOf[BinaryOp]
    assert(rootOp.operator == "*", s"Expected '*', got ${rootOp.operator}")

    assert(rootOp.left.isInstanceOf[BinaryOp], s"Expected BinaryOp on left, got ${rootOp.left.getClass}")
    val leftOp = rootOp.left.asInstanceOf[BinaryOp]
    assert(leftOp.operator == "+", s"Expected '+', got ${leftOp.operator}")

    val leftLeft = leftOp.left
    val leftRight = leftOp.right
    assert(leftLeft == Reference("A",1), s"Expected Reference(A,1), got $leftLeft")
    assert(leftRight == Reference("B",2), s"Expected Reference(B,2), got $leftRight")

    val rightAst = rootOp.right
    assert(rightAst == Reference("C",10), s"Expected Reference(C,10), got $rightAst")
  }

  test("Parse invalid formula with mismatched parentheses") {
    // Try parse "1+(2*3"
    val result = safeParse("1+(2*3")
    assert(result.isFailure, "Expected failure for mismatched parentheses")
    assert(result.failed.get.isInstanceOf[RuntimeException],
      s"Expected RuntimeException, got ${result.failed.get.getClass}")
  }

  test("Parse formula with unexpected token") {
    // "1&2" should fail because '&' is not a recognized operator or token
    val result = safeParse("1&2")
    assert(result.isFailure, "Expected failure due to unexpected token '&'")
    assert(result.failed.get.getMessage.contains("Unexpected token: &"),
      s"Expected error message about unexpected token, got ${result.failed.get.getMessage}")
  }

  test("Parse empty formula should fail") {
    val result = safeParse("")
    // Empty string doesn't match any token, so likely fails when popping from stacks.
    assert(result.isFailure, "Expected failure for empty formula")
  }

  test("Parse formula with unknown operator from registry") {
    // Suppose '^' is not registered
    val result = safeParse("2^3")
    assert(result.isFailure, "Expected failure because '^' is not known operator")
    assert(result.failed.get.getMessage.contains("Unexpected token"),
      s"Expected error message about unexpected token, got ${result.failed.get.getMessage}")
  }

  test("Parse complex numeric formula") {
    // Check a more complex numeric-only formula: "(10+20)/(5-1)"
    val ast = parser.parse("(10+20)/(5-1)")
    // AST should be: BinaryOp("/", BinaryOp("+", Number(10), Number(20)), BinaryOp("-", Number(5), Number(1)))
    assert(ast.isInstanceOf[BinaryOp], s"Expected BinaryOp at root, got ${ast.getClass}")
    val rootOp = ast.asInstanceOf[BinaryOp]
    assert(rootOp.operator == "/", s"Expected '/', got ${rootOp.operator}")

    val numerator = rootOp.left
    val denominator = rootOp.right

    assert(numerator.isInstanceOf[BinaryOp], s"Expected BinaryOp in numerator, got ${numerator.getClass}")
    val numOp = numerator.asInstanceOf[BinaryOp]
    assert(numOp.operator == "+", s"Expected '+', got ${numOp.operator}")
    assert(numOp.left == Number(10.0), s"Expected left of '+' is Number(10.0), got ${numOp.left}")
    assert(numOp.right == Number(20.0), s"Expected right of '+' is Number(20.0), got ${numOp.right}")

    assert(denominator.isInstanceOf[BinaryOp], s"Expected BinaryOp in denominator, got ${denominator.getClass}")
    val denOp = denominator.asInstanceOf[BinaryOp]
    assert(denOp.operator == "-", s"Expected '-', got ${denOp.operator}")
    assert(denOp.left == Number(5.0), s"Expected left of '-' is Number(5.0), got ${denOp.left}")
    assert(denOp.right == Number(1.0), s"Expected right of '-' is Number(1.0), got ${denOp.right}")
  }
}
