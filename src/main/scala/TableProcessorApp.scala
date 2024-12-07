import Headers.DefaultHeaderHandler
import filters.{ColumnFilter, Filter, FilterHandler, IsEmptyFilter, IsNonEmptyFilter}
import loaders.CsvLoader
import parsing.objects.Table
import ranges.DefaultRangeSelector

import scala.collection.mutable
import scala.util.Try
object TableProcessorApp {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      println(
        """
          |Usage:
          |  --input-file [FILE]              the input CSV file (required)
          |  --input-separator [STRING]       the separator for input (optional, defaults to ",")
          |  --filter [COLUMN] [OPERATOR] [VALUE] apply a value filter
          |  --filter-is-not-empty [COLUMN]   filter out lines with empty cells on column
          |  --filter-is-empty [COLUMN]       filter out lines with non-empty cells on column
          |  --range [FROM] [TO]              specify a range of the table to output
          |  --headers                        include headers in the output
        """.stripMargin)
      sys.exit(1)
    }

    val argsMap = mutable.Map[String, String]()
    val filters = mutable.ListBuffer[Filter]()
    var range: Option[(String, String)] = None
    var includeHeaders = false

    try {
      var i = 0
      while (i < args.length) {
        args(i) match {
          case "--input-file" =>
            argsMap("input-file") = args(i + 1)
            i += 2

          case "--input-separator" =>
            argsMap("input-separator") = args(i + 1)
            i += 2

          case "--filter" =>
            val column = args(i + 1)
            val operator = args(i + 2)
            val value = args(i + 3)
            filters += ColumnFilter(column, operator, value.toDouble)
            i += 4

          case "--filter-is-not-empty" =>
            filters += IsNonEmptyFilter(args(i + 1))
            i += 2

          case "--filter-is-empty" =>
            filters += IsEmptyFilter(args(i + 1))
            i += 2

          case "--range" =>
            range = Some(args(i + 1), args(i + 2))
            i += 3

          case "--headers" =>
            includeHeaders = true
            i += 1

          case other =>
            throw new IllegalArgumentException(s"Unknown argument: $other")
        }
      }

      if (!argsMap.contains("input-file")) {
        throw new IllegalArgumentException("The --input-file argument is required.")
      }

      val filePath = argsMap("input-file")
      val separator = argsMap.getOrElse("input-separator", ",")
      val loader = new CsvLoader()
      val rawData = loader.load(filePath, separator)

      val table = new Table(rawData.length, rawData.head.length)
      for ((row, rowIndex) <- rawData.zipWithIndex) {
        for ((cell, colIndex) <- row.zipWithIndex) {
          val colName = ('A' + colIndex).toChar.toString
          table.setCell(colName, rowIndex + 1, cell)
        }
      }

      val rangeSelector = new DefaultRangeSelector()
      val headerHandler = new DefaultHeaderHandler()

      val (startRow, endRow, startCol, endCol) = range match {
        case Some((from, to)) =>
          rangeSelector.calculateRange(from, to, rawData.length, rawData.head.length)
        case None =>
          (0, rawData.length - 1, 0, rawData.head.length - 1)
      }

      val filteredData = rawData.zipWithIndex.filter { case (row, idx) =>
        val rowMap = row.zipWithIndex.map { case (cell, colIdx) =>
          val colName = ('A' + colIdx).toChar.toString
          colName -> cell
        }.toMap
        filters.forall(_.apply(rowMap))
      }

      val filteredRowsWithIndices = filteredData.map { case (row, originalIndex) => (originalIndex + 1, row) }

      val rangedRows = filteredRowsWithIndices.filter { case (originalIndex, _) =>
        originalIndex >= startRow + 1 && originalIndex <= endRow + 1
      }

      val result = rangedRows.map { case (_, row) =>
        row.slice(startCol, endCol + 1)
      }

      // Print headers or an empty header row
      if (includeHeaders) {
        println(headerHandler.generateHeaders(startCol, endCol))
      } else {
        val emptyHeader = (startCol to endCol).map(_ => " ").mkString(", ")
        println(emptyHeader)
      }

      // Print rows
      rangedRows.foreach { case (originalIndex, row) =>
        if (includeHeaders) {
          println(headerHandler.formatRow(originalIndex, row.slice(startCol, endCol + 1)))
        } else {
          println(row.slice(startCol, endCol + 1).mkString(", "))
        }
      }

    } catch {
      case e: IllegalArgumentException =>
        println(s"Error: ${e.getMessage}")
        sys.exit(1)
      case e: RuntimeException =>
        println(s"Unexpected error: ${e.getMessage}")
        sys.exit(1)
    }
  }
}



