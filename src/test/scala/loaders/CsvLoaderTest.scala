package loaders

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe

import java.io.PrintWriter
import scala.io.Source
import java.nio.file.{Files, Paths}

import org.scalatest.funsuite.AnyFunSuite
import scala.util.Try

class CsvLoaderTest extends AnyFunSuite {

  private val loader = new CsvLoader()

  // Helper function to safely load and return Try for assertion without matchers.
  private def safeLoad(path: String, separator: String = ","): Try[List[List[String]]] = {
    Try(loader.load(path, separator))
  }

  test("Load valid CSV with default separator should return expected rows and columns") {
    val result = loader.load("src/test/resources/valid.csv")
    assert(result.size == 3, s"Expected 3 lines, got ${result.size}")
    assert(result.head == List("name","age","city"), s"Header line mismatch: ${result.head}")
    assert(result(1) == List("John","30","New York"), s"First data line mismatch: ${result(1)}")
    assert(result(2) == List("Alice","25","Los Angeles"), s"Second data line mismatch: ${result(2)}")
  }

  test("Load CSV with a non-existing file should throw a RuntimeException") {
    val result = safeLoad("src/test/resources/non_existing.csv")
    assert(result.isFailure, "Expected loading a non-existing file to fail.")
    assert(result.failed.get.isInstanceOf[RuntimeException],
      s"Expected RuntimeException, got ${result.failed.get.getClass.getName}")
  }

  test("Load empty CSV file should return an empty List") {
    val result = loader.load("src/test/resources/empty.csv")
    assert(result.isEmpty, s"Expected empty result, got $result")
  }

  test("Load CSV file with blank lines and spaces should parse lines correctly") {
    val result = loader.load("src/test/resources/blank_lines.csv")
    // Let's check how many non-empty lines should be returned
    // The file might have extra blank lines. After trimming, empty lines should still show up as empty lists.
    // According to the implementation: lines are read and split, empty lines become List("").
    // If we want to consider whether this is correct or not is up to definition.
    // We'll just assert the count we expect. Suppose we expect all lines returned:
    // The file lines (including blank):
    // 1: "" (blank line)
    // 2: "John,30"
    // 3: ", "
    // 4: "Alice,25"
    // 5: "" (blank line)
    // After split by ",":
    // 1: List("")          // blank line
    // 2: List("John","30")
    // 3: List("", "") or List("") depending on trimming (with trailing spaces: ", " -> List("", ""))
    // 4: List("Alice","25")
    // 5: List("")

    assert(result.size == 5, s"Expected 5 lines including blanks, got ${result.size}")
    assert(result(1) == List("John","30"), s"Second line mismatch: ${result(1)}")
    assert(result(3) == List("Alice","25"), s"Fourth line mismatch: ${result(3)}")
  }

  test("Load CSV with custom separator should split correctly") {
    val result = loader.load("src/test/resources/custom_sep.csv", separator = ":")
    // custom_sep.csv lines:
    // key:value     -> List("key", "value")
    // one:1         -> List("one","1")
    // two:2         -> List("two","2")
    assert(result.size == 3, s"Expected 3 lines, got ${result.size}")
    assert(result.head == List("key","value"), s"Header line mismatch: ${result.head}")
    assert(result(1) == List("one","1"), s"First data line mismatch: ${result(1)}")
    assert(result(2) == List("two","2"), s"Second data line mismatch: ${result(2)}")
  }

  test("Load CSV with trailing spaces should trim correctly") {
    // Let's create a file content mentally:
    // "a , b " -> after split by "," and trim: List("a", "b")
    // For test: create a test file with trailing spaces or just trust we have it at `trailing_spaces.csv`:
    // trailing_spaces.csv content:
    // a , b
    // c,  d
    val result = loader.load("src/test/resources/trailing_spaces.csv")
    assert(result.size == 2, s"Expected 2 lines, got ${result.size}")
    assert(result(0) == List("a","b"), s"Line 1 trimming failed: ${result(0)}")
    assert(result(1) == List("c","d"), s"Line 2 trimming failed: ${result(1)}")
  }

  test("Load CSV with no explicit separator arg should default to comma") {
    // Using the valid.csv again but explicitly testing default arg behavior:
    val result = loader.load("src/test/resources/valid.csv")
    // Same assertions as first test:
    assert(result.size == 3, s"Expected 3 lines, got ${result.size}")
    assert(result.head == List("name","age","city"), s"Header line mismatch: ${result.head}")
  }

  test("Load CSV from file with mixed empty columns should handle them") {
    // Suppose we have a file `mixed_columns.csv`:
    // "col1,,col3"
    // ",,"
    // After splitting by comma and trimming:
    // ["col1","","col3"]
    // ["","",""]
    val result = loader.load("src/test/resources/mixed_columns.csv")
    assert(result.size == 2, s"Expected 2 lines, got ${result.size}")
    assert(result.head == List("col1","","col3"), s"First line mismatch: ${result.head}")
    assert(result(1) == List("","",""), s"Second line mismatch: ${result(1)}")
  }

  test("Load CSV with a single line should still return that line") {
    // single_line.csv:
    // "just one line"
    val result = loader.load("src/test/resources/single_line.csv")
    assert(result.size == 1, s"Expected 1 line, got ${result.size}")
    assert(result.head == List("just one line"), s"Mismatch: ${result.head}")
  }

  test("Load CSV with special characters should not fail") {
    // special_chars.csv:
    // "na©me, 😀age, cit¥"
    val result = loader.load("src/test/resources/special_chars.csv")
    assert(result.size == 1, s"Expected 1 line, got ${result.size}")
    assert(result.head == List("na©me","😀age","cit¥"), s"Mismatch with special chars: ${result.head}")
  }

  // More tests can be added if needed for further edge cases.
}

