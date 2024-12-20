package filters

import filters.operatorRegistry.DefaultFilterOperatorRegistry
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.Assertions._

class FilterTest extends AnyFunSuite with Matchers {

  // Test cases for ColumnFilter

  test("ColumnFilter with '==' operator returns true when cell value equals filter value") {
    val operatorRegistry = DefaultFilterOperatorRegistry
    val filter = ColumnFilter("age", "==", 30.0, operatorRegistry)
    val row = Map("age" -> "30")
    val result: Boolean = filter(row) // Pass Map[String, String] directly
    assert(result)
  }


  test("ColumnFilter with '==' operator returns false when cell value not equal filter value") {
    val filter = ColumnFilter("age", "==", 30.0)
    val row = Map("age" -> "31")
    val result = filter(row)
    assert(result == false, s"Expected false since 31 != 30, got $result")
  }

  test("ColumnFilter with '<' operator returns true when cell value < filter value") {
    val filter = ColumnFilter("price", "<", 100.0)
    val row = Map("price" -> "99.99")
    val result = filter(row)
    assert(result == true, s"Expected true since 99.99 < 100.0, got $result")
  }

  test("ColumnFilter with '<' operator returns false when cell value >= filter value") {
    val filter = ColumnFilter("price", "<", 100.0)
    val row = Map("price" -> "100")
    val result = filter(row)
    assert(result == false, s"Expected false since 100 >= 100.0, got $result")
  }

  test("ColumnFilter with '>' operator returns true when cell value > filter value") {
    val filter = ColumnFilter("age", ">", 20.0)
    val row = Map("age" -> "25")
    val result = filter(row)
    assert(result == true, s"Expected true since 25 > 20.0, got $result")
  }

  test("ColumnFilter with '>' operator returns false when cell value <= filter value") {
    val filter = ColumnFilter("age", ">", 20.0)
    val row = Map("age" -> "20")
    val result = filter(row)
    assert(result == false, s"Expected false since 20 <= 20.0, got $result")
  }

  test("ColumnFilter with '<=' operator returns true when cell value <= filter value") {
    val filter = ColumnFilter("height", "<=", 180.0)
    val row = Map("height" -> "180")
    val result = filter(row)
    assert(result == true, s"Expected true since 180 <= 180, got $result")
  }

  test("ColumnFilter with '<=' operator returns false when cell value > filter value") {
    val filter = ColumnFilter("height", "<=", 180.0)
    val row = Map("height" -> "181")
    val result = filter(row)
    assert(result == false, s"Expected false since 181 > 180, got $result")
  }

  test("ColumnFilter with '>=' operator returns true when cell value >= filter value") {
    val filter = ColumnFilter("weight", ">=", 70.0)
    val row = Map("weight" -> "80")
    val result = filter(row)
    assert(result == true, s"Expected true since 80 >= 70, got $result")
  }

  test("ColumnFilter with '>=' operator returns false when cell value < filter value") {
    val filter = ColumnFilter("weight", ">=", 70.0)
    val row = Map("weight" -> "69.9")
    val result = filter(row)
    assert(result == false, s"Expected false since 69.9 < 70, got $result")
  }

  test("ColumnFilter with '!=' operator returns true when cell value not equal filter value") {
    val filter = ColumnFilter("score", "!=", 50.0)
    val row = Map("score" -> "51")
    val result = filter(row)
    assert(result == true, s"Expected true since 51 != 50, got $result")
  }

  test("ColumnFilter with '!=' operator returns false when cell value equals filter value") {
    val filter = ColumnFilter("score", "!=", 50.0)
    val row = Map("score" -> "50")
    val result = filter(row)
    assert(result == false, s"Expected false since 50 == 50, got $result")
  }

  test("ColumnFilter returns false when column is not present") {
    val filter = ColumnFilter("nonexistent", "==", 1.0)
    val row = Map("someColumn" -> "1")
    val result = filter(row)
    assert(result == false, s"Expected false since column 'nonexistent' not in row, got $result")
  }

  test("ColumnFilter returns false when cell is empty") {
    val filter = ColumnFilter("age", "==", 30.0)
    val row = Map("age" -> "")
    val result = filter(row)
    assert(result == false, s"Expected false for empty cell, got $result")
  }

  test("ColumnFilter returns false when cell is invalid number") {
    val filter = ColumnFilter("age", "==", 30.0)
    val row = Map("age" -> "notANumber")
    val result = filter(row)
    assert(result == false, s"Expected false for invalid number, got $result")
  }


  // Test cases for IsEmptyFilter

  test("IsEmptyFilter returns true for empty cell") {
    val filter = IsEmptyFilter("name")
    val row = Map("name" -> "")
    val result = filter(row)
    assert(result == true, s"Expected true since cell is empty, got $result")
  }

  test("IsEmptyFilter returns true when column not present") {
    val filter = IsEmptyFilter("missingCol")
    val row = Map("name" -> "John")
    val result = filter(row)
    assert(result == true, s"Expected true since column is missing (treated as empty), got $result")
  }

  test("IsEmptyFilter returns false for non-empty cell") {
    val filter = IsEmptyFilter("name")
    val row = Map("name" -> "Alice")
    val result = filter(row)
    assert(result == false, s"Expected false since cell is not empty, got $result")
  }


  // Test cases for IsNonEmptyFilter

  test("IsNonEmptyFilter returns true for non-empty cell") {
    val filter = IsNonEmptyFilter("name")
    val row = Map("name" -> "Bob")
    val result = filter(row)
    assert(result == true, s"Expected true since cell has 'Bob', got $result")
  }

  test("IsNonEmptyFilter returns false for empty cell") {
    val filter = IsNonEmptyFilter("name")
    val row = Map("name" -> "")
    val result = filter(row)
    assert(result == false, s"Expected false since cell is empty, got $result")
  }

  test("IsNonEmptyFilter returns false when column not present") {
    val filter = IsNonEmptyFilter("age")
    val row = Map("name" -> "Carol")
    val result = filter(row)
    assert(result == false, s"Expected false since column is missing, got $result")
  }

}
