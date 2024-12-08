import cliHandlers.{FilterHandler, HeadersHandler, HelpHandler, InputFileHandler, InputSeparatorHandler, IsEmptyFilterHandler, IsNonEmptyFilterHandler, OutputFileHandler, OutputFormatHandler, OutputSeparatorHandler, ParameterChain, ParameterHandler, RangeHandler, StdoutHandler}
import prettyPrinting.PrettyPrinterFactory
import evaluation.Table
import loaders.CsvLoader
import outputOptions.OutputHandlerFactory
import ranges.DefaultRangeSelector

object TableProcessorApp {
  def main(args: Array[String]): Unit = {
    // Assume we have our ParameterChain and handlers as implemented previously
    val handlers: List[ParameterHandler] = List(
      new HelpHandler(),
      new InputFileHandler(),
      new InputSeparatorHandler(),
      new FilterHandler(),
      new IsNonEmptyFilterHandler(),
      new IsEmptyFilterHandler(),
      new RangeHandler(),
      new HeadersHandler(),
      new OutputFormatHandler(),
      new OutputSeparatorHandler(),
      new OutputFileHandler(),
      new StdoutHandler()
    )

    val chain = new ParameterChain(handlers)
    val config = chain.parse(args.toList)

    if (config.showHelp || config.inputFile.isEmpty) {
      println("Usage:")
      println(chain.help)
      return
    }

    val filePath = config.inputFile.get
    val separator = config.inputSeparator
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
    val prettyPrinter = PrettyPrinterFactory.getPrinter(config.outputFormat)

    val filteredData = rawData.zipWithIndex.filter { case (row, idx) =>
      val rowMap = row.zipWithIndex.map { case (cell, colIdx) =>
        val colName = ('A' + colIdx).toChar.toString
        colName -> cell
      }.toMap
      config.filters.forall(_.apply(rowMap))
    }

    val filteredRowsWithIndices = filteredData.map { case (row, originalIndex) => (originalIndex + 1, row) }

    val (startRow, endRow, startCol, endCol) = config.range match {
      case Some((from, to)) =>
        rangeSelector.calculateRange(from, to, rawData.length, rawData.head.length)
      case None =>
        (0, rawData.length - 1, 0, rawData.head.length - 1)
    }

    val rangedRows = filteredRowsWithIndices.filter { case (originalIndex, _) =>
      originalIndex >= startRow + 1 && originalIndex <= endRow + 1
    }

    val result = rangedRows.map { case (index, row) =>
      val sliced = (startCol to endCol).map { colIndex =>
        val colName = ('A' + colIndex).toChar.toString
        table.getCellValue(colName, index)
      }
      (index, sliced)
    }

    val headersOpt = if (config.includeHeaders) Some((startCol to endCol).map(i => ('A' + i).toChar.toString)) else None
    val content = prettyPrinter.printTable(result, headersOpt, config.outputSeparator)

    val outputHandler = OutputHandlerFactory.getOutputHandler(
      Map(
        "stdout" -> config.stdout.toString,
        "output-file" -> config.outputFile.getOrElse("")
      )
    )
    outputHandler.write(content)
  }
}




