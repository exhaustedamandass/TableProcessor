package ranges

import org.scalatest.funsuite.AnyFunSuite

class DefaultRangeSelectorTest extends AnyFunSuite {

  private val selector = new DefaultRangeSelector

  test("Calculate range for a simple in-bound range A1 to B2") {
    val range = selector.calculateRange("A1", "B2", maxRows = 10, maxCols = 10)
    // A -> col 0,  B -> col 1
    // Row 1 -> index 0, Row 2 -> index 1
    // So expected: startRow = 0, endRow = 1, startCol = 0, endCol = 1
    assert(range.startRow == 0, s"Expected startRow = 0, got ${range.startRow}")
    assert(range.endRow == 1, s"Expected endRow = 1, got ${range.endRow}")
    assert(range.startCol == 0, s"Expected startCol = 0, got ${range.startCol}")
    assert(range.endCol == 1, s"Expected endCol = 1, got ${range.endCol}")
  }

  test("Calculate range when from and to are reversed (B2 to A1)") {
    val range = selector.calculateRange("B2", "A1", maxRows = 10, maxCols = 10)
    // B2 -> col = 1, row = 1 (0-based)
    // A1 -> col = 0, row = 0 (0-based)
    // Expected: startRow = 0, endRow = 1, startCol = 0, endCol = 1
    assert(range.startRow == 0, s"Expected startRow = 0, got ${range.startRow}")
    assert(range.endRow == 1, s"Expected endRow = 1, got ${range.endRow}")
    assert(range.startCol == 0, s"Expected startCol = 0, got ${range.startCol}")
    assert(range.endCol == 1, s"Expected endCol = 1, got ${range.endCol}")
  }

  test("Calculate range with multi-letter columns (AA10 to AB20)") {
    val range = selector.calculateRange("AA10", "AB20", maxRows = 100, maxCols = 100)
    // AA -> 26 (0-based index = 26)
    // AB -> 27 (0-based index = 27)
    // Row 10 -> index 9, Row 20 -> index 19
    // Expected: startRow = 9, endRow = 19, startCol = 26, endCol = 27
    assert(range.startRow == 9, s"Expected startRow = 9, got ${range.startRow}")
    assert(range.endRow == 19, s"Expected endRow = 19, got ${range.endRow}")
    assert(range.startCol == 26, s"Expected startCol = 26, got ${range.startCol}")
    assert(range.endCol == 27, s"Expected endCol = 27, got ${range.endCol}")
  }

  test("Calculate range with reversed rows (A10 to A1)") {
    val range = selector.calculateRange("A10", "A1", maxRows = 20, maxCols = 5)
    // A -> col 0
    // Row 10 -> index 9, Row 1 -> index 0
    // Expected: startRow = 0, endRow = 9 (clamped within maxRows 20)
    // startCol = 0, endCol = 0 since same column
    assert(range.startRow == 0, s"Expected startRow = 0, got ${range.startRow}")
    assert(range.endRow == 9, s"Expected endRow = 9, got ${range.endRow}")
    assert(range.startCol == 0, s"Expected startCol = 0, got ${range.startCol}")
    assert(range.endCol == 0, s"Expected endCol = 0, got ${range.endCol}")
  }

  test("Calculate range with reversed columns (C1 to A1)") {
    val range = selector.calculateRange("C1", "A1", maxRows = 10, maxCols = 10)
    // C -> col 2, A -> col 0
    // Row 1 -> index 0
    // Expected: startRow = 0, endRow = 0, startCol = 0, endCol = 2
    assert(range.startRow == 0, s"Expected startRow = 0, got ${range.startRow}")
    assert(range.endRow == 0, s"Expected endRow = 0, got ${range.endRow}")
    assert(range.startCol == 0, s"Expected startCol = 0, got ${range.startCol}")
    assert(range.endCol == 2, s"Expected endCol = 2, got ${range.endCol}")
  }

  test("Calculate range with same cell reference (A1 to A1)") {
    val range = selector.calculateRange("A1", "A1", maxRows = 10, maxCols = 10)
    // Both are A1 -> col=0, row=0
    // Expected startRow=0, endRow=0, startCol=0, endCol=0
    assert(range.startRow == 0, s"Expected startRow = 0, got ${range.startRow}")
    assert(range.endRow == 0, s"Expected endRow = 0, got ${range.endRow}")
    assert(range.startCol == 0, s"Expected startCol = 0, got ${range.startCol}")
    assert(range.endCol == 0, s"Expected endCol = 0, got ${range.endCol}")
  }

  test("Calculate range with large column name (ZZ10 to AAA20)") {
    // ZZ: Z=26, so ZZ = 26*26 + 26 = 26*26=676+26=702, zero-based=701
    // AAA: For triple letters: A=1, so AAA = 1*(26^2)+1*(26^1)+1*(26^0)
    //     = 1*676 + 1*26 + 1*1 = 676+26+1=703 zero-based=702
    // Rows: 10->9, 20->19
    // Expected: startRow=9, endRow=19, startCol=701, endCol=702
    val range = selector.calculateRange("ZZ10", "AAA20", maxRows = 1000, maxCols = 2000)
    assert(range.startRow == 9, s"Expected startRow=9, got ${range.startRow}")
    assert(range.endRow == 19, s"Expected endRow=19, got ${range.endRow}")
    assert(range.startCol == 701, s"Expected startCol=701, got ${range.startCol}")
    assert(range.endCol == 702, s"Expected endCol=702, got ${range.endCol}")
  }

  test("Calculate range with out-of-bound negative reference") {
    // Suppose from is A0 (invalid, row=0 => index=-1) and to is A-5 (even more invalid)
    // Let's see how the code handles it.
    // Row 0 => index = -1
    // Row -5 => index = -6
    // min(-6,-1)=-6 clamp(-6,0,maxRow)=0, max(-6,-1)=-1 clamp(-1,0,maxRow)=0
    // So both clamp to 0, collapsing to a single row.
    val range = selector.calculateRange("A0", "A-5", maxRows = 10, maxCols = 10)
    assert(range.startRow == 0, s"Expected startRow=0 after clamping, got ${range.startRow}")
    assert(range.endRow == 0, s"Expected endRow=0 after clamping, got ${range.endRow}")
    // Columns are A=0
    assert(range.startCol == 0, s"Expected startCol=0, got ${range.startCol}")
    assert(range.endCol == 0, s"Expected endCol=0, got ${range.endCol}")
  }

  test("Calculate range with very large indices that exceed limits") {
    // From: X10000, To: Z20000
    // X=23 zero-based, Z=25 zero-based
    // row 10000->9999 index, row 20000->19999 index
    // clamp(9999,0, maxRows-1), if maxRows=500 => clamp(9999,0,499)=499
    // clamp(19999,0,499)=499
    val range = selector.calculateRange("X10000", "Z20000", maxRows = 500, maxCols = 30)
    // columns:
    // X=23, Z=25
    // clamp(23,0,29)=23, clamp(25,0,29)=25
    assert(range.startRow == 499, s"Expected startRow=499, got ${range.startRow}")
    assert(range.endRow == 499, s"Expected endRow=499, got ${range.endRow}")
    assert(range.startCol == 23, s"Expected startCol=23, got ${range.startCol}")
    assert(range.endCol == 25, s"Expected endCol=25, got ${range.endCol}")
  }

}
