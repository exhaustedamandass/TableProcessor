package cliHandlers

import filters.Filter

case class AppConfig(
  inputFile: Option[String] = None,
  inputSeparator: String = ",",
  filters: Seq[Filter] = Seq.empty,
  range: Option[(String, String)] = None,
  includeHeaders: Boolean = false,
  outputFormat: String = "csv",
  outputSeparator: String = ",",
  outputFile: Option[String] = None,
  stdout: Boolean = true,
  showHelp: Boolean = false
)
