package tableProcessor

import cliHandlers.config.AppConfig
import cliHandlers.parameterChains.ParameterChain
import evaluation.Table
import loaders.CsvLoader
import prettyPrinting.PrettyPrinterRegistry
import prettyPrinting.prettyPrinterRegistries.DefaultPrettyPrinterRegistry
import ranges.{DefaultRangeSelector, GridRange}
import tableProcessor.ColumnNameConverter.columnIndexToName

class TableProcessor(config: AppConfig, chain: ParameterChain, registry: PrettyPrinterRegistry = DefaultPrettyPrinterRegistry) {

  def process(): String = {
    val filePath = config.inputFile.get
    val separator = config.inputSeparator
    val loader = new CsvLoader()

    val rawData = try {
      loader.load(filePath, separator)
    } catch {
      case e: Exception =>
        println(s"Error: Unable to load data from '$filePath'. ${e.getMessage}")
        return ""
    }

    val table = new Table(rawData.length, rawData.head.length)
    for ((row, rowIndex) <- rawData.zipWithIndex) {
      for ((cell, colIndex) <- row.zipWithIndex) {
        val colName = columnIndexToName(colIndex)
        table.setCell(colName, rowIndex + 1, cell)
      }
    }

    val rangeSelector = new DefaultRangeSelector()
    val prettyPrinter = registry.getPrinter(config.outputFormat) // <- registry used here

    // Apply filters
    val filteredData = rawData.zipWithIndex.filter { case (row, idx) =>
      val rowMap = row.zipWithIndex.map { case (cell, colIdx) =>
        val colName = columnIndexToName(colIdx)
        colName -> cell
      }.toMap
      config.filters.forall(_.apply(rowMap))
    }

    val filteredRowsWithIndices = filteredData.map { case (row, originalIndex) => (originalIndex + 1, row) }

    val gridRange = config.range match {
      case Some((from, to)) =>
        rangeSelector.calculateRange(from, to, rawData.length, rawData.head.length)
      case None =>
        GridRange(0, rawData.length - 1, 0, rawData.head.length - 1)
    }

    val (startRow, endRow, startCol, endCol) = (gridRange.startRow, gridRange.endRow, gridRange.startCol, gridRange.endCol)

    val rangedRows = filteredRowsWithIndices.filter { case (originalIndex, _) =>
      originalIndex >= startRow + 1 && originalIndex <= endRow + 1
    }

    val result = rangedRows.map { case (index, row) =>
      val sliced = (startCol to endCol).map { colIndex =>
        val colName = columnIndexToName(colIndex)
        table.getCellValue(colName, index)
      }
      (index, sliced)
    }

    val headersOpt = if (config.includeHeaders) Some((startCol to endCol).map(columnIndexToName)) else None
    prettyPrinter.printTable(result, headersOpt, config.outputSeparator)
  }

  def help: String = chain.help
}

