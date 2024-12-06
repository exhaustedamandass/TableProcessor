import filters.{ColumnFilter, Filter, FilterHandler, IsEmptyFilter, IsNonEmptyFilter}
import handlers.DefaultHandler
import handlers.input.{InputFileHandler, InputSeparatorHandler}
import loaders.CsvLoader
import parsing.objects.Table

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
        """.stripMargin)
      sys.exit(1)
    }

    val argsMap = mutable.Map[String, String]()
    val filters = mutable.ListBuffer[Filter]()

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
            if (i + 3 >= args.length) {
              throw new IllegalArgumentException("Incomplete filter specification. Expected --filter [COLUMN] [OPERATOR] [VALUE].")
            }
            val column = args(i + 1)
            val operator = args(i + 2)
            val value = args(i + 3)

            // Validate operator
            if (!Set("<", ">", "<=", ">=", "==", "!=").contains(operator)) {
              throw new IllegalArgumentException(s"Invalid operator: $operator. Allowed operators: <, >, <=, >=, ==, !=")
            }

            // Validate value as a number
            val numericValue = Try(value.toDouble).getOrElse {
              throw new IllegalArgumentException(s"Invalid numeric value: $value")
            }

            filters += ColumnFilter(column, operator, numericValue)
            i += 4

          case "--filter-is-non-empty" =>
            if (i + 1 >= args.length) {
              throw new IllegalArgumentException("Missing column for --filter-is-non-empty")
            }
            filters += IsNonEmptyFilter(args(i + 1))
            i += 2

          case "--filter-is-empty" =>
            if (i + 1 >= args.length) {
              throw new IllegalArgumentException("Missing column for --filter-is-empty")
            }
            filters += IsEmptyFilter(args(i + 1))
            i += 2

          case other =>
            throw new IllegalArgumentException(s"Unknown argument: $other")
        }
      }

      // Ensure input file is provided
      if (!argsMap.contains("input-file")) {
        throw new IllegalArgumentException("The --input-file argument is required.")
      }

      // Display parsed CLI arguments
      println("Parsed CLI arguments:")
      argsMap.foreach { case (key, value) => println(s"$key -> $value") }
      println("Parsed Filters:")
      filters.foreach(println)

      // Load and process the CSV
      val filePath = argsMap("input-file")
      val separator = argsMap.getOrElse("input-separator", ",")
      val loader = new CsvLoader()
      val rawData = loader.load(filePath, separator)

      val table = new Table(rawData.length, rawData.headOption.map(_.length).getOrElse(0))
      for ((row, rowIndex) <- rawData.zipWithIndex) {
        for ((cell, colIndex) <- row.zipWithIndex) {
          val colName = ('A' + colIndex).toChar.toString
          table.setCell(colName, rowIndex + 1, cell)
        }
      }

      println("\nFiltered Table:")
      rawData.indices.foreach { rowIndex =>
        val rowMap = rawData.head.indices.map { colIndex =>
          val colName = ('A' + colIndex).toChar.toString
          colName -> table.getCellValue(colName, rowIndex + 1)
        }.toMap

        if (filters.forall(_.apply(rowMap))) {
          println(rowMap.values.mkString(", "))
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


