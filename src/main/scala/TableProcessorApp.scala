import Headers.DefaultHeaderHandler
import filters.{ColumnFilter, Filter, FilterHandler, IsEmptyFilter, IsNonEmptyFilter}
import loaders.CsvLoader
import outputOptions.PrettyPrinterFactory
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
          |  --output-format (csv|md)         the format of the output (optional, defaults to "csv")
          |  --output-separator [STRING]      for CSV output: the separator in the output file (optional, defaults to ",")
        """.stripMargin)
      sys.exit(1)
    }

    val argsMap = mutable.Map[String, String]()
    val filters = mutable.ListBuffer[Filter]()
    var range: Option[(String, String)] = None
    var includeHeaders = false
    var outputFormat = "csv"
    var outputSeparator = ","

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

          case "--output-format" =>
            outputFormat = args(i + 1)
            i += 2

          case "--output-separator" =>
            outputSeparator = args(i + 1)
            i += 2

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

      val rangeSelector = new DefaultRangeSelector()

      val filteredData = rawData.zipWithIndex.filter { case (row, idx) =>
        val rowMap = row.zipWithIndex.map { case (cell, colIdx) =>
          val colName = ('A' + colIdx).toChar.toString
          colName -> cell
        }.toMap
        filters.forall(_.apply(rowMap))
      }

      val filteredRowsWithIndices = filteredData.map { case (row, originalIndex) => (originalIndex + 1, row) }

      val (startRow, endRow, startCol, endCol) = range match {
        case Some((from, to)) =>
          rangeSelector.calculateRange(from, to, rawData.length, rawData.head.length)
        case None =>
          (0, rawData.length - 1, 0, rawData.head.length - 1)
      }

      val rangedRows = filteredRowsWithIndices.filter { case (originalIndex, _) =>
        originalIndex >= startRow + 1 && originalIndex <= endRow + 1
      }

      val result = rangedRows.map { case (index, row) =>
        (index, row.slice(startCol, endCol + 1))
      }

      val headers = if (includeHeaders) Some((startCol to endCol).map(i => ('A' + i).toChar.toString)) else None

      val prettyPrinter = PrettyPrinterFactory.getPrinter(outputFormat)

      // Prepare rows based on whether headers are included
      val rowsForOutput = result.map {
        case (rowIndex, row) if includeHeaders => (rowIndex, row) // Include row indices when headers are specified
        case (_, row) => (-1, row) // Use only the row data (exclude indices)
      }

      // Skip placeholder indices in the `PrettyPrinter`
      val output = prettyPrinter.printTable(
        rowsForOutput.filter {
          case (-1, _) if !includeHeaders => true // Allow rows without indices when headers are not specified
          case (rowIndex, _) => rowIndex != -1
        },
        headers,
        outputSeparator
      )

      println(output)

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




